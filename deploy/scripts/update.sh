#!/usr/bin/env bash
# Redeploys part of the app after the first deployment.
#
# Usage: update.sh <backend|frontend|secrets|all>
#   backend   rebuild the Spring Boot image and roll it out
#   frontend  rebuild the React app and upload it
#   secrets   push config.env secrets to Secrets Manager and restart the backend
#   all       backend + frontend
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"
load_config
require_stack

scripts="$DEPLOY_DIR/scripts"
target="${REMAINING_ARGS[0]:-}"

restart_backend() {
  step "Restarting backend containers to pick up new secrets"
  aws ecs update-service --cluster "$(stack_output ClusterName)" \
    --service "$(stack_output ServiceName)" --force-new-deployment >/dev/null
  info "Rolling restart started. Watch it with: ./deploy/scripts/status.sh"
}

case "$target" in
  backend)
    "$scripts/2-build-backend.sh"
    "$scripts/3-deploy-infra.sh"
    ;;
  frontend)
    "$scripts/5-deploy-frontend.sh"
    ;;
  secrets)
    "$scripts/1-setup.sh"
    restart_backend
    ;;
  all)
    "$scripts/2-build-backend.sh"
    "$scripts/3-deploy-infra.sh"
    "$scripts/5-deploy-frontend.sh"
    ;;
  *)
    die "Usage: update.sh <backend|frontend|secrets|all>"
    ;;
esac
