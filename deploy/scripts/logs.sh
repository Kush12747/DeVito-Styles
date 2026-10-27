#!/usr/bin/env bash
# Shows backend logs from CloudWatch.
#
# Usage: logs.sh [--follow] [--since 1h]
source "$(dirname "$0")/lib.sh"
load_config
require_stack

follow=()
since="1h"
while [ $# -gt 0 ]; do
  case "$1" in
    -f|--follow) follow=(--follow); shift ;;
    --since) since="${2:?--since needs a value like 30m or 2h}"; shift 2 ;;
    *) die "Usage: logs.sh [--follow] [--since 1h]" ;;
  esac
done

aws logs tail "$(stack_output LogGroupName)" \
  --log-stream-name-prefix backend \
  --since "$since" \
  --format short \
  "${follow[@]+"${follow[@]}"}"
