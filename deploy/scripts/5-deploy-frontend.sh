#!/usr/bin/env bash
# Step 5: builds the React app against the live API and uploads it to S3,
# then clears the CloudFront cache.
source "$(dirname "$0")/lib.sh"
parse_common_flags "$@"
load_config
require_stack

site_url="$(stack_output SiteUrl)"
bucket="$(stack_output FrontendBucketName)"
distribution_id="$(stack_output DistributionId)"
client_dir="$REPO_ROOT/Client"

step "Building frontend for $site_url"
(
  cd "$client_dir"
  npm ci
  # The site and API share the CloudFront domain, so API calls go to /api/* on it
  VITE_API_URL="$site_url" npm run build
)
ok "Built $client_dir/dist"

step "Uploading to s3://$bucket"
# Hashed asset files never change, so browsers can cache them for a year
aws s3 sync "$client_dir/dist/assets" "s3://$bucket/assets" --delete \
  --cache-control "public, max-age=31536000, immutable"
# index.html and other root files must always be revalidated
aws s3 sync "$client_dir/dist" "s3://$bucket" --delete --exclude "assets/*" \
  --cache-control "no-cache"
ok "Uploaded"

step "Invalidating CloudFront cache"
aws cloudfront create-invalidation --distribution-id "$distribution_id" --paths "/*" \
  --query 'Invalidation.Id' --output text >/dev/null
ok "Invalidation started (takes a minute or two to finish)"

printf '\n%sFrontend deployed:%s %s\n' "$C_GREEN" "$C_OFF" "$site_url"
