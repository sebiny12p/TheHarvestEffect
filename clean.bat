@echo off
setlocal
echo Cleaning compiled binaries and temporary artifacts...
if exist "bin" rd /s /q "bin"
if exist "sources.txt" del /f /q "sources.txt"
if exist "*.dat" del /f /q "*.dat"
echo [CLEAN] Workspace is clean. Sources only remain.
