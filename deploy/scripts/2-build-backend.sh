#!/usr/bin/env bash
# Step 2: builds the Spring Boot Docker image and pushes it to ECR.
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"
load_config
load_state

backend_dir="$REPO_ROOT/Server/DeVito-Styles"
[ -f "$backend_dir/src/main/resources/google-credentials.json" ] \
  || die "google-credentials.json is missing from src/main/resources. The app can't start without it."

registry="$(ecr_registry)"
tag="$(git_short_sha)-$(date +%Y%m%d%H%M%S)"
image="$registry/$ECR_REPO:$tag"

step "Logging in to ECR"
aws ecr get-login-password | docker login --username AWS --password-stdin "$registry" >/dev/null
ok "Logged in to $registry"

step "Building backend image ($tag)"
# Fargate runs x86_64; --platform keeps builds on Apple Silicon compatible.
DOCKER_BUILDKIT=1 docker build \
  --platform linux/amd64 \
  -f "$DEPLOY_DIR/docker/backend.Dockerfile" \
  -t "$image" \
  -t "$registry/$ECR_REPO:latest" \
  "$backend_dir"

step "Pushing to ECR"
docker push "$image"
docker push "$registry/$ECR_REPO:latest"

save_state BACKEND_IMAGE_URI "$image"
ok "Pushed $image"
printf '\n%sBackend image ready.%s Next: ./deploy/scripts/3-deploy-infra.sh\n' "$C_GREEN" "$C_OFF"
