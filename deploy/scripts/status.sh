#!/usr/bin/env bash
# Shows the site URL, backend container health and recent deployment events.
source "$(dirname "$0")/lib.sh"
load_config

if ! stack_exists; then
  info "Stack '$STACK_NAME' is not deployed in $AWS_REGION."
  exit 0
fi

step "Stack"
aws cloudformation describe-stacks --stack-name "$STACK_NAME" \
  --query 'Stacks[0].[StackStatus, LastUpdatedTime || CreationTime]' --output text

step "URLs"
info "Site:           $(stack_output SiteUrl)"
info "Stripe webhook: $(stack_output StripeWebhookUrl)"

step "Backend service"
aws ecs describe-services --cluster "$(stack_output ClusterName)" \
  --services "$(stack_output ServiceName)" \
  --query 'services[0].{Desired:desiredCount, Running:runningCount, Pending:pendingCount, Rollout:deployments[0].rolloutState}' \
  --output table

step "Recent service events"
aws ecs describe-services --cluster "$(stack_output ClusterName)" \
  --services "$(stack_output ServiceName)" \
  --query 'services[0].events[0:5].[createdAt, message]' --output text
