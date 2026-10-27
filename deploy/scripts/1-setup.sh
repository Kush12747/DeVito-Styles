#!/usr/bin/env bash
# Step 1: creates the ECR repositories and stores app secrets in Secrets Manager.
# Safe to re-run; it updates the secret with the current config.env values.
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"
load_config
load_state

step "Creating ECR repositories"
lifecycle_policy='{"rules":[{"rulePriority":1,"description":"Keep last 10 images","selection":{"tagStatus":"any","countType":"imageCountMoreThan","countNumber":10},"action":{"type":"expire"}}]}'
for repo in "$ECR_REPO" "$DB_INIT_REPO"; do
  if aws ecr describe-repositories --repository-names "$repo" >/dev/null 2>&1; then
    ok "$repo already exists"
  else
    aws ecr create-repository --repository-name "$repo" \
      --image-scanning-configuration scanOnPush=true >/dev/null
    # Keep only the 10 most recent images to limit storage costs
    aws ecr put-lifecycle-policy --repository-name "$repo" \
      --lifecycle-policy-text "$lifecycle_policy" >/dev/null
    ok "Created $repo"
  fi
done

step "Storing app secrets in Secrets Manager ($APP_SECRET_NAME)"
for key in MAIL_HOST MAIL_PORT MAIL_USERNAME MAIL_PASSWORD CLOUD_NAME API_KEY API_SECRET STRIPE_SECRET_KEY; do
  [ -n "${!key:-}" ] || die "$key is empty in config.env."
done

# JWT signing key: use config.env if set, otherwise keep the one already in
# Secrets Manager (so redeploys don't log everyone out), otherwise generate one.
if [ -z "${JWT_SECRET:-}" ]; then
  existing_json="$(aws secretsmanager get-secret-value --secret-id "$APP_SECRET_NAME" \
    --query SecretString --output text 2>/dev/null || true)"
  JWT_SECRET="$(printf '%s' "$existing_json" | node "$DEPLOY_DIR/scripts/helpers/read-json-key.js" JWT_SECRET)"
  if [ -z "$JWT_SECRET" ]; then
    JWT_SECRET="$(node -e 'console.log(require("crypto").randomBytes(48).toString("hex"))')"
    info "Generated a new JWT signing key"
  fi
fi
[ "${#JWT_SECRET}" -ge 32 ] || die "JWT_SECRET must be at least 32 characters."
export JWT_SECRET

mkdir -p "$DEPLOY_DIR/.build"
secret_file="$DEPLOY_DIR/.build/app-secret.json"
trap 'rm -f "$secret_file"' EXIT

# Build the JSON with Node so special characters in values are escaped.
# A blank webhook secret gets a placeholder so the container can still start.
STRIPE_WEBHOOK_SECRET="${STRIPE_WEBHOOK_SECRET:-not-configured}" node "$DEPLOY_DIR/scripts/helpers/write-app-secret.js" "$secret_file"

if aws secretsmanager describe-secret --secret-id "$APP_SECRET_NAME" >/dev/null 2>&1; then
  aws secretsmanager put-secret-value --secret-id "$APP_SECRET_NAME" \
    --secret-string "file://$secret_file" >/dev/null
  ok "Updated existing secret"
else
  aws secretsmanager create-secret --name "$APP_SECRET_NAME" \
    --description "DeVito Styles app secrets (mail, Cloudinary, Stripe)" \
    --secret-string "file://$secret_file" >/dev/null
  ok "Created secret"
fi

APP_SECRET_ARN="$(aws secretsmanager describe-secret --secret-id "$APP_SECRET_NAME" --query ARN --output text)"
save_state APP_SECRET_ARN "$APP_SECRET_ARN"

step "Origin verification secret"
if [ -z "${ORIGIN_VERIFY_SECRET:-}" ]; then
  ORIGIN_VERIFY_SECRET="$(node -e 'console.log(require("crypto").randomBytes(32).toString("hex"))')"
  save_state ORIGIN_VERIFY_SECRET "$ORIGIN_VERIFY_SECRET"
  ok "Generated and saved to deploy/.state.env"
else
  ok "Already set"
fi

printf '\n%sSetup complete.%s Next: ./deploy/scripts/2-build-backend.sh\n' "$C_GREEN" "$C_OFF"
