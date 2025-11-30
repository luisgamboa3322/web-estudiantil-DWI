@echo off
REM ============================================
REM Script para Detener la Aplicacion
REM ============================================

echo ========================================
echo   DETENER - Web Estudiantil
echo ========================================
echo.

echo Buscando proceso de la aplicacion...

REM Buscar procesos Java que contengan "demo" o "webestudiantil"
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr /I "PID"') do (
    wmic process where "ProcessId=%%a" get CommandLine 2>nul | findstr /I "demo webestudiantil" >nul
    if !errorlevel! equ 0 (
        echo.
        echo Deteniendo proceso:
        echo   PID: %%a
        taskkill /PID %%a /F
        echo [OK] Proceso detenido
    )
)

echo.
echo ========================================
echo   Aplicacion detenida
echo ========================================
pause
