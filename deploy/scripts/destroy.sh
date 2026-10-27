#!/usr/bin/env bash
# Tears down the deployment.
#
# Deletes the CloudFormation stack and everything in it. RDS takes a final
# snapshot first (kept until you delete it). With --all, also deletes the
# ECR repositories and the app secret, which live outside the stack.
#
# Usage: destroy.sh [--all]
source "$(dirname "$0")/lib.sh"
load_config

delete_all=0
for arg in "$@"; do
  case "$arg" in
    --all) delete_all=1 ;;
    *) die "Usage: destroy.sh [--all]" ;;
  esac
done

step "This will permanently delete the '$STACK_NAME' deployment in $AWS_REGION"
info "- The site, load balancer, backend containers and CloudFront distribution"
info "- The RDS database (a final snapshot is kept)"
[ "$delete_all" = 1 ] && info "- The ECR image repositories and the '$APP_SECRET_NAME' secret"
echo
read -r -p "Type the stack name ($STACK_NAME) to confirm: " typed
[ "$typed" = "$STACK_NAME" ] || die "Name didn't match. Nothing was deleted."

if stack_exists; then
  bucket="$(stack_output FrontendBucketName)"
  if [ -n "$bucket" ] && [ "$bucket" != "None" ]; then
    step "Emptying s3://$bucket"
    aws s3 rm "s3://$bucket" --recursive >/dev/null || true
    ok "Emptied"
  fi

  step "Deleting stack (CloudFront makes this take 10-20 minutes)"
  aws cloudformation delete-stack --stack-name "$STACK_NAME"
  aws cloudformation wait stack-delete-complete --stack-name "$STACK_NAME"
  ok "Stack deleted"
else
  info "Stack '$STACK_NAME' doesn't exist; skipping."
fi

if [ "$delete_all" = 1 ]; then
  step "Deleting ECR repositories"
  for repo in "$ECR_REPO" "$DB_INIT_REPO"; do
    if aws ecr delete-repository --repository-name "$repo" --force >/dev/null 2>&1; then
      ok "Deleted $repo"
    else
      info "$repo not found"
    fi
  done

  step "Scheduling app secret for deletion"
  if aws secretsmanager delete-secret --secret-id "$APP_SECRET_NAME" \
       --recovery-window-in-days 7 >/dev/null 2>&1; then
    ok "Secret will be deleted in 7 days (restore it with 'aws secretsmanager restore-secret' before then)"
  else
    info "Secret not found"
  fi

  rm -f "$STATE_FILE"
  ok "Removed deploy/.state.env"
fi

step "Leftover database snapshots"
info "RDS kept a final snapshot. Snapshots cost about \$0.095/GB-month. To list them:"
info "  aws rds describe-db-snapshots --db-instance-identifier ${PROJECT_NAME}-db --query 'DBSnapshots[].DBSnapshotIdentifier'"
info "To delete one:"
info "  aws rds delete-db-snapshot --db-snapshot-identifier <id>"
