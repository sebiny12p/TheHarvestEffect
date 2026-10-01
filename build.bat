@echo off
setlocal
echo ======================================================
echo       THE HARVEST EFFECT ENGINE COMPILATION BUILD     
echo ======================================================

if not exist "bin" (
    mkdir bin
)

echo [1/2] Scanning source files in src/...
dir /s /b src\*.java > sources.txt

echo [2/2] Invoking Java compiler (UTF-8, release 8)...
javac -encoding UTF-8 --release 8 -d bin @sources.txt
if %errorlevel% neq 0 (
    echo [WARN] --release flag unsupported or failed. Retrying with -source 1.8 -target 1.8...
    javac -encoding UTF-8 -source 1.8 -target 1.8 -d bin @sources.txt
)

if %errorlevel% equ 0 (
    echo [SUCCESS] Build completed successfully.
    echo Binaries output to: bin\
    if exist sources.txt del sources.txt
    exit /b 0
) else (
    echo [ERROR] Compilation failed.
    if exist sources.txt del sources.txt
    exit /b 1
)
