#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f "bin/com/seb/harvesteffect/test/HarvestEffectTestSuite.class" ]; then
    echo "[INFO] Binaries not found. Triggering automated build..."
    ./build.sh
fi

java -cp bin com.seb.harvesteffect.test.HarvestEffectTestSuite "$@"
