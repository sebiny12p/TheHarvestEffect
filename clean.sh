#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Cleaning compiled binaries and temporary artifacts..."
rm -rf bin sources.txt *.dat
echo "[CLEAN] Workspace is clean. Sources only remain."
