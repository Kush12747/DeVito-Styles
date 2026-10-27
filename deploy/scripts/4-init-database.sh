#!/usr/bin/env bash
# Step 4: loads the schema and seed data into RDS.
#
# Runs Server/DeVito-Styles/sql/production-schema.sql and initial-data.sql
# from a one-off Fargate task inside the VPC, so the database never needs
# to be publicly reachable.
#
# WARNING: production-schema.sql DROPS and recreates the devitostyles
# database. Only run this on first deploy or when you mean to reset all data.
#
# Usage: 4-init-database.sh --confirm-drop
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"
load_config
require_stack

confirmed=0
for arg in "${REMAINING_ARGS[@]+"${REMAINING_ARGS[@]}"}"; do
  case "$arg" in
    --confirm-drop) confirmed=1 ;;
    *) die "Unknown option: $arg" ;;
  esac
done
[ "$confirmed" = 1 ] || die "This deletes all data in the database. Re-run with --confirm-drop if that's what you want."

registry="$(ecr_registry)"
image="$registry/$DB_INIT_REPO:latest"
build_dir="$DEPLOY_DIR/.build/db-init"

step "Building database initializer image"
rm -rf "$build_dir" && mkdir -p "$build_dir"
cp "$REPO_ROOT/Server/DeVito-Styles/sql/production-schema.sql" \
   "$REPO_ROOT/Server/DeVito-Styles/sql/initial-data.sql" \
   "$DEPLOY_DIR/docker/db-init.sh" \
   "$build_dir/"
cp "$DEPLOY_DIR/docker/db-init.Dockerfile" "$build_dir/Dockerfile"

aws ecr get-login-password | docker login --username AWS --password-stdin "$registry" >/dev/null
DOCKER_BUILDKIT=1 docker build --platform linux/amd64 -t "$image" "$build_dir"
docker push "$image"
ok "Pushed $image"

step "Registering one-off task definition"
task_file="$DEPLOY_DIR/.build/db-init-task.json"
PROJECT_NAME="$PROJECT_NAME" \
EXECUTION_ROLE_ARN="$(stack_output TaskExecutionRoleArn)" \
DB_INIT_IMAGE="$image" \
DB_HOST="$(stack_output DatabaseEndpoint)" \
DB_SECRET_ARN="$(stack_output DatabaseSecretArn)" \
LOG_GROUP="$(stack_output LogGroupName)" \
AWS_REGION="$AWS_REGION" \
  node "$DEPLOY_DIR/scripts/helpers/db-init-task.js" "$task_file"

task_def_arn="$(aws ecs register-task-definition --cli-input-json "file://$task_file" \
  --query taskDefinition.taskDefinitionArn --output text)"
ok "Registered $task_def_arn"

step "Running database initialization"
cluster="$(stack_output ClusterName)"
subnets="$(stack_output PublicSubnetIds)"
security_group="$(stack_output ServiceSecurityGroupId)"

# Public IP lets the task pull its image from ECR without a NAT gateway.
# The security group still only allows the task to talk out, not in.
task_arn="$(aws ecs run-task \
  --cluster "$cluster" \
  --launch-type FARGATE \
  --task-definition "$task_def_arn" \
  --network-configuration "awsvpcConfiguration={subnets=[$subnets],securityGroups=[$security_group],assignPublicIp=ENABLED}" \
  --query 'tasks[0].taskArn' --output text)"
[ -n "$task_arn" ] && [ "$task_arn" != "None" ] || die "Failed to start the task."
info "Task: $task_arn"
info "Waiting for it to finish (usually 1-3 minutes)..."

aws ecs wait tasks-stopped --cluster "$cluster" --tasks "$task_arn"

exit_code="$(aws ecs describe-tasks --cluster "$cluster" --tasks "$task_arn" \
  --query 'tasks[0].containers[0].exitCode' --output text)"

step "Task logs"
aws logs tail "$(stack_output LogGroupName)" --log-stream-name-prefix db-init --since 30m || true

if [ "$exit_code" = "0" ]; then
  printf '\n%sDatabase initialized.%s\n' "$C_GREEN" "$C_OFF"
else
  reason="$(aws ecs describe-tasks --cluster "$cluster" --tasks "$task_arn" \
    --query 'tasks[0].stoppedReason' --output text)"
  die "Database initialization failed (exit code: $exit_code, reason: $reason)."
fi
