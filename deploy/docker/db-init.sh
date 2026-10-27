#!/usr/bin/env bash
# Runs inside the db-init container (ECS one-off task).
# Expects DB_HOST, DB_USER and DB_PASSWORD in the environment.
set -euo pipefail

: "${DB_HOST:?DB_HOST is required}"
: "${DB_USER:?DB_USER is required}"
: "${DB_PASSWORD:?DB_PASSWORD is required}"

export MYSQL_PWD="$DB_PASSWORD"   # keeps the password off the command line
mysql_cmd=(mysql --host="$DB_HOST" --user="$DB_USER" --connect-timeout=20)

echo "Waiting for MySQL at ${DB_HOST}..."
for attempt in $(seq 1 30); do
  if "${mysql_cmd[@]}" -e "SELECT 1" >/dev/null 2>&1; then
    break
  fi
  if [ "$attempt" -eq 30 ]; then
    echo "MySQL did not become reachable" >&2
    exit 1
  fi
  sleep 10
done

echo "Applying production-schema.sql (drops and recreates the devitostyles database)..."
"${mysql_cmd[@]}" < /sql/production-schema.sql

echo "Loading initial-data.sql..."
"${mysql_cmd[@]}" < /sql/initial-data.sql

echo "Tables created:"
"${mysql_cmd[@]}" -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'devitostyles'"
echo "Database initialization complete."
