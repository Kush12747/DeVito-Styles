#!/usr/bin/env bash
# Verifies local tools, AWS credentials and config before deploying.
source "$(dirname "$0")/lib.sh"

step "Checking local tools"
missing=0
for tool in aws docker node npm git; do
  if command -v "$tool" >/dev/null 2>&1; then
    ok "$tool"
  else
    printf '    ✗ %s not found\n' "$tool"
    missing=1
  fi
done
[ "$missing" = 0 ] || die "Install the missing tools above (see deploy/README.md)."

aws --version 2>&1 | grep -q "aws-cli/2" || die "AWS CLI v2 is required (found: $(aws --version 2>&1))."
docker info >/dev/null 2>&1 || die "Docker is installed but not running. Start Docker Desktop and retry."
ok "Docker daemon is running"

step "Checking configuration"
load_config
ok "Loaded config.env (region $AWS_REGION, stack $STACK_NAME)"

for key in MAIL_HOST MAIL_PORT MAIL_USERNAME MAIL_PASSWORD CLOUD_NAME API_KEY API_SECRET STRIPE_SECRET_KEY; do
  [ -n "${!key:-}" ] || die "$key is empty in config.env."
done
ok "Required app secrets are filled in"
[ -n "${STRIPE_WEBHOOK_SECRET:-}" ] || info "STRIPE_WEBHOOK_SECRET is blank. That's expected for the first deploy."

creds="$REPO_ROOT/Server/DeVito-Styles/src/main/resources/google-credentials.json"
[ -f "$creds" ] || die "Missing $creds. The backend loads it at startup, so it must exist before building."
ok "google-credentials.json found"

step "Checking AWS credentials"
identity="$(aws sts get-caller-identity --query '[Account, Arn]' --output text 2>/dev/null)" \
  || die "AWS credentials aren't working. Run 'aws configure' (or 'aws sso login') first."
ok "Authenticated as: $identity"

printf '\n%sAll checks passed.%s\n' "$C_GREEN" "$C_OFF"
