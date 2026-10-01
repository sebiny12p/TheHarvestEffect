#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "======================================================"
echo "    THE HARVEST EFFECT ENGINE COMPILATION BUILD       "
echo "======================================================"

mkdir -p bin

echo "[1/2] Scanning source files in src/..."
find src -name "*.java" > sources.txt

echo "[2/2] Invoking Java compiler (UTF-8, release 8)..."
if javac -encoding UTF-8 --release 8 -d bin @sources.txt 2>/dev/null; then
    echo "[SUCCESS] Build completed successfully with --release 8."
else
    echo "[WARN] --release flag unsupported or failed. Retrying standard compilation..."
    javac -encoding UTF-8 -d bin @sources.txt
    echo "[SUCCESS] Build completed successfully."
fi

rm -f sources.txt
echo "Binaries output to: bin/"
