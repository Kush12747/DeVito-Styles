#!/usr/bin/env bash
# Shared helpers for the deployment scripts. Sourced, not run directly.

set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPO_ROOT="$(cd "$DEPLOY_DIR/.." && pwd)"
CONFIG_FILE="$DEPLOY_DIR/config.env"
STATE_FILE="$DEPLOY_DIR/.state.env"
# shellcheck disable=SC2034  # used by the scripts that source this file
TEMPLATE_FILE="$DEPLOY_DIR/cloudformation/devito-styles.yml"

# Git Bash on Windows rewrites arguments that start with "/" into Windows
# paths (e.g. the log group /ecs/devito-styles). Turn that off.
export MSYS_NO_PATHCONV=1
export MSYS2_ARG_CONV_EXCL="*"
# Print AWS CLI output directly instead of opening a pager
export AWS_PAGER=""

# ---- Output -------------------------------------------------------------
if [ -t 1 ]; then
  C_BOLD=$'\033[1m'; C_GOLD=$'\033[33m'; C_RED=$'\033[31m'; C_GREEN=$'\033[32m'; C_OFF=$'\033[0m'
else
  C_BOLD=""; C_GOLD=""; C_RED=""; C_GREEN=""; C_OFF=""
fi

step() { printf '\n%s==> %s%s\n' "$C_BOLD$C_GOLD" "$*" "$C_OFF"; }
info() { printf '    %s\n' "$*"; }
ok()   { printf '%s    ✓ %s%s\n' "$C_GREEN" "$*" "$C_OFF"; }
die()  { printf '%sError: %s%s\n' "$C_RED" "$*" "$C_OFF" >&2; exit 1; }

# ---- Config & state -----------------------------------------------------
load_config() {
  [ -f "$CONFIG_FILE" ] || die "Missing $CONFIG_FILE. Copy config.example.env to config.env and fill it in."
  set -a
  # shellcheck disable=SC1090
  . "$CONFIG_FILE"
  set +a

  : "${AWS_REGION:?AWS_REGION must be set in config.env}"
  : "${PROJECT_NAME:?PROJECT_NAME must be set in config.env}"
  : "${STACK_NAME:?STACK_NAME must be set in config.env}"

  if [ -n "${AWS_PROFILE:-}" ]; then export AWS_PROFILE; else unset AWS_PROFILE; fi
  export AWS_REGION AWS_DEFAULT_REGION="$AWS_REGION"

  # shellcheck disable=SC2034  # used by the scripts that source this file
  ECR_REPO="${PROJECT_NAME}-backend"
  # shellcheck disable=SC2034
  DB_INIT_REPO="${PROJECT_NAME}-db-init"
  # shellcheck disable=SC2034
  APP_SECRET_NAME="${PROJECT_NAME}/app"
}

load_state() {
  if [ -f "$STATE_FILE" ]; then
    # shellcheck disable=SC1090
    . "$STATE_FILE"
  fi
}

# save_state KEY VALUE - stores a value in .state.env (gitignored)
save_state() {
  local key="$1" value="$2" tmp
  tmp="$(mktemp)"
  if [ -f "$STATE_FILE" ]; then grep -v "^${key}=" "$STATE_FILE" > "$tmp" || true; fi
  printf '%s=%q\n' "$key" "$value" >> "$tmp"
  mv "$tmp" "$STATE_FILE"
}

# ---- AWS helpers --------------------------------------------------------
account_id() {
  aws sts get-caller-identity --query Account --output text
}

ecr_registry() {
  printf '%s.dkr.ecr.%s.amazonaws.com' "$(account_id)" "$AWS_REGION"
}

stack_exists() {
  aws cloudformation describe-stacks --stack-name "$STACK_NAME" >/dev/null 2>&1
}

# stack_output KEY - prints one CloudFormation output value
stack_output() {
  aws cloudformation describe-stacks --stack-name "$STACK_NAME" \
    --query "Stacks[0].Outputs[?OutputKey=='$1'].OutputValue" --output text
}

require_stack() {
  stack_exists || die "Stack '$STACK_NAME' doesn't exist yet. Run 3-deploy-infra.sh first."
}

# confirm "message" - asks y/N unless --yes / ASSUME_YES=1
confirm() {
  if [ "${ASSUME_YES:-0}" = "1" ]; then return 0; fi
  local reply
  read -r -p "$1 [y/N] " reply
  [[ "$reply" =~ ^[Yy]$ ]] || die "Cancelled."
}

# Parses common flags; sets ASSUME_YES. Leaves other args in REMAINING_ARGS.
parse_common_flags() {
  REMAINING_ARGS=()
  for arg in "$@"; do
    case "$arg" in
      -y|--yes) ASSUME_YES=1 ;;
      *) REMAINING_ARGS+=("$arg") ;;
    esac
  done
}

git_short_sha() {
  git -C "$REPO_ROOT" rev-parse --short HEAD 2>/dev/null || date +%Y%m%d%H%M%S
}
