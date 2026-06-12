#!/usr/bin/env bash
# pre-commit local hook: run php-cs-fixer over apps/web if the toolchain is present.
# Skips gracefully (exit 0) when PHP / php-cs-fixer is not installed so the hook
# never blocks contributors who only touch the desktop / mobile apps.
set -euo pipefail

cd "$(dirname "$0")/../apps/web"

if ! command -v php >/dev/null 2>&1; then
  echo "php not found — skipping php-cs-fixer (install PHP 8.2+ to enable)."
  exit 0
fi

if [ -x vendor/bin/php-cs-fixer ]; then
  FIXER="vendor/bin/php-cs-fixer"
elif command -v php-cs-fixer >/dev/null 2>&1; then
  FIXER="php-cs-fixer"
else
  echo "php-cs-fixer not installed (run 'composer install' in apps/web) — skipping."
  exit 0
fi

PHP_CS_FIXER_IGNORE_ENV=1 "$FIXER" fix --dry-run --diff --config=.php-cs-fixer.dist.php
