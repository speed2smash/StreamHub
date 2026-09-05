#!/usr/bin/env bash
set -euo pipefail
gradle :app:assembleDebug
printf '\nAPK: app/build/outputs/apk/debug/app-debug.apk\n'
