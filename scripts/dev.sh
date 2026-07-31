#!/usr/bin/env bash

set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
database_path="$project_dir/data/planit_db"
backend_pid=""
frontend_pid=""

cleanup() {
  if [[ -n "$backend_pid" ]]; then
    kill "$backend_pid" 2>/dev/null || true
  fi
  if [[ -n "$frontend_pid" ]]; then
    kill "$frontend_pid" 2>/dev/null || true
  fi
}

trap cleanup EXIT INT TERM

mkdir -p "$project_dir/data"

(
  cd "$project_dir/backend"
  PLANIT_DATABASE_PATH="$database_path" mvn spring-boot:run
) &
backend_pid=$!

(
  cd "$project_dir/frontend"
  npm run dev
) &
frontend_pid=$!

wait "$backend_pid"
wait "$frontend_pid"
