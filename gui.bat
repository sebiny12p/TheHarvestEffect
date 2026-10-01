@echo off
setlocal
if not exist "bin\com\seb\harvesteffect\Main.class" (
    echo [INFO] Binaries not found. Triggering automated build...
    call "%~dp0build.bat"
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed. Cannot launch GUI.
        exit /b 1
    )
)

echo [LAUNCH] Starting The Harvest Effect Java Swing GUI Dashboard...
start "The Harvest Effect Dashboard" java -cp "%~dp0bin" com.seb.harvesteffect.Main --gui
