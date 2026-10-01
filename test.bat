@echo off
setlocal
if not exist "bin\com\seb\harvesteffect\test\HarvestEffectTestSuite.class" (
    echo [INFO] Binaries not found. Triggering automated build...
    call "%~dp0build.bat"
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed. Cannot run test suite.
        exit /b 1
    )
)

java -cp "%~dp0bin" com.seb.harvesteffect.test.HarvestEffectTestSuite %*
