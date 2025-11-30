@echo off
REM ============================================
REM Script de Despliegue Local - Web Estudiantil
REM ============================================
REM Este script despliega la aplicacion en el servidor local

set PROFILE=dev
if not "%1"=="" set PROFILE=%1

echo ========================================
echo   DESPLIEGUE LOCAL - Web Estudiantil
echo ========================================
echo.

REM Verificar que el JAR existe
echo [1/6] Verificando archivo JAR...
if not exist "target\*.jar" (
    echo [ERROR] No se encontro el archivo JAR
    echo Ejecutar primero: .\scripts\build.bat
    pause
    exit /b 1
)
for %%f in (target\*.jar) do (
    if not "%%~nxf"=="%%~nxf:sources=%" goto :skip
    if not "%%~nxf"=="%%~nxf:javadoc=%" goto :skip
    set JAR_FILE=%%f
    echo [OK] JAR encontrado: %%~nxf
    goto :found
    :skip
)
:found
echo.

REM Verificar Java
echo [2/6] Verificando Java...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java no esta instalado
    pause
    exit /b 1
)
echo [OK] Java encontrado
echo.

REM Verificar MySQL
echo [3/6] Verificando MySQL...
tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I /N "mysqld.exe">NUL
if %errorlevel% equ 0 (
    echo [OK] MySQL esta ejecutandose
) else (
    echo [ADVERTENCIA] No se detecto MySQL ejecutandose
    echo Asegurate de que MySQL este iniciado en localhost:3306
    set /p continue="Continuar de todas formas? (s/n): "
    if /i not "%continue%"=="s" exit /b 1
)
echo.

REM Configurar variables de entorno
echo [4/6] Configurando variables de entorno...
set SPRING_PROFILE=%PROFILE%
echo [OK] Perfil de Spring: %PROFILE%
echo.

REM Crear directorio de logs si no existe
echo [5/6] Preparando directorios...
if not exist "logs" mkdir logs
echo [OK] Directorio de logs listo
echo.

REM Iniciar aplicacion
echo [6/6] Iniciando aplicacion...
echo.
echo ========================================
echo   Aplicacion iniciando...
echo ========================================
echo.
echo Perfil activo: %PROFILE%
echo Puerto: 8083
echo.
echo URLs de acceso:
echo   - Aplicacion:  http://localhost:8083
echo   - Health:      http://localhost:8083/actuator/health
echo.
echo Presiona Ctrl+C para detener la aplicacion
echo.
echo ========================================
echo.

REM Ejecutar la aplicacion
java -jar "%JAR_FILE%" --spring.profiles.active=%PROFILE%

echo.
echo Aplicacion detenida
pause
