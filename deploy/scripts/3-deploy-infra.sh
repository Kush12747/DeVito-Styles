#!/usr/bin/env bash
# Step 3: creates or updates the CloudFormation stack (network, database,
# load balancer, ECS service, S3 bucket, CloudFront).
#
# Usage: 3-deploy-infra.sh [--desired-count N] [--yes]
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"
load_config
load_state

desired_count=1
set -- "${REMAINING_ARGS[@]+"${REMAINING_ARGS[@]}"}"
while [ $# -gt 0 ]; do
  case "$1" in
    --desired-count) desired_count="${2:?--desired-count needs a number}"; shift 2 ;;
    *) die "Unknown option: $1" ;;
  esac
done

: "${APP_SECRET_ARN:?Run 1-setup.sh first}"
: "${ORIGIN_VERIFY_SECRET:?Run 1-setup.sh first}"
: "${BACKEND_IMAGE_URI:?Run 2-build-backend.sh first}"

if ! stack_exists; then
  step "Creating stack '$STACK_NAME' in $AWS_REGION"
  info "This creates billable resources (RDS, Fargate, load balancer, CloudFront)."
  info "Rough cost with default sizes: about \$55-70/month. See deploy/README.md."
  info "First creation takes about 15-25 minutes (RDS and CloudFront are slow)."
  confirm "Create the stack now?"
else
  step "Updating stack '$STACK_NAME'"
fi

aws cloudformation deploy \
  --stack-name "$STACK_NAME" \
  --template-file "$TEMPLATE_FILE" \
  --capabilities CAPABILITY_IAM \
  --no-fail-on-empty-changeset \
  --tags Project="$PROJECT_NAME" \
  --parameter-overrides \
    ProjectName="$PROJECT_NAME" \
    BackendImageUri="$BACKEND_IMAGE_URI" \
    AppSecretArn="$APP_SECRET_ARN" \
    OriginVerifySecret="$ORIGIN_VERIFY_SECRET" \
    DesiredCount="$desired_count" \
    ClaudeModel="${CLAUDE_MODEL:-claude-opus-5-5}" \
    TaskCpu="${TASK_CPU:-512}" \
    TaskMemory="${TASK_MEMORY:-1024}" \
    DbInstanceClass="${DB_INSTANCE_CLASS:-db.t4g.micro}" \
    DbAllocatedStorage="${DB_ALLOCATED_STORAGE:-20}"

ok "Stack is up to date (backend containers: $desired_count)"
info "Site URL: $(stack_output SiteUrl)"
