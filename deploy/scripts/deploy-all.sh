#!/usr/bin/env bash
# Runs the full deployment in order. On the first run it also loads the
# database before starting the backend.
#
# Usage: deploy-all.sh [--yes] [--reset-db]
#   --yes       skip confirmation prompts
#   --reset-db  re-run the schema and seed scripts on an existing stack
#               (DELETES ALL DATA)
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"

reset_db=0
for arg in "${REMAINING_ARGS[@]+"${REMAINING_ARGS[@]}"}"; do
  case "$arg" in
    --reset-db) reset_db=1 ;;
    *) die "Unknown option: $arg" ;;
  esac
done

scripts="$DEPLOY_DIR/scripts"
yes_flag=()
[ "${ASSUME_YES:-0}" = "1" ] && yes_flag=(--yes)

"$scripts/check-prereqs.sh"
load_config

"$scripts/1-setup.sh"
"$scripts/2-build-backend.sh"

if ! stack_exists; then
  # First deploy: create everything with the backend stopped, load the
  # database, then start the backend so it never runs against an empty DB.
  "$scripts/3-deploy-infra.sh" --desired-count 0 "${yes_flag[@]+"${yes_flag[@]}"}"
  "$scripts/4-init-database.sh" --confirm-drop
  "$scripts/3-deploy-infra.sh" --desired-count 1
else
  if [ "$reset_db" = 1 ]; then
    confirm "Reset the database? This deletes all users, appointments and orders."
    "$scripts/4-init-database.sh" --confirm-drop
  fi
  "$scripts/3-deploy-infra.sh" --desired-count 1
fi

"$scripts/5-deploy-frontend.sh"

step "Deployment complete"
info "Site:           $(stack_output SiteUrl)"
info "Stripe webhook: $(stack_output StripeWebhookUrl)"
if [ -z "${ANTHROPIC_API_KEY:-}" ]; then
  info ""
  info "ANTHROPIC_API_KEY is blank, so the chat assistant replies that it's unavailable."
  info "Add a key to config.env and run ./deploy/scripts/update.sh secrets to turn it on."
fi
if [ -z "${STRIPE_WEBHOOK_SECRET:-}" ]; then
  info ""
  info "Next: register the Stripe webhook URL above in the Stripe dashboard,"
  info "put its signing secret in config.env as STRIPE_WEBHOOK_SECRET, then run:"
  info "  ./deploy/scripts/update.sh secrets"
fi
