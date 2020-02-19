@echo off
if not "%1"=="am_admin" (powershell start -verb runas '%0' am_admin & exit /b)
    start tkonline-win.exe
pause