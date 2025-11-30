@echo off
REM ============================================
REM Script de Construccion - Web Estudiantil
REM ============================================
REM Este script compila la aplicacion y genera el JAR ejecutable

echo ========================================
echo   BUILD - Web Estudiantil
echo ========================================
echo.

REM Verificar que Maven esta instalado
echo [1/5] Verificando Maven...
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Maven no esta instalado o no esta en el PATH
    echo Descargar desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
mvn --version | findstr "Apache Maven"
echo [OK] Maven encontrado
echo.

REM Verificar que Java esta instalado
echo [2/5] Verificando Java...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java no esta instalado o no esta en el PATH
    echo Descargar JDK 17+: https://adoptium.net/
    pause
    exit /b 1
)
java -version 2>&1 | findstr "version"
echo [OK] Java encontrado
echo.

REM Limpiar builds anteriores
echo [3/5] Limpiando builds anteriores...
call mvn clean
if %errorlevel% neq 0 (
    echo [ERROR] Fallo la limpieza
    pause
    exit /b 1
)
echo [OK] Limpieza completada
echo.

REM Ejecutar tests
echo [4/5] Ejecutando tests...
echo (Presiona Ctrl+C si quieres saltar los tests)
call mvn test
if %errorlevel% neq 0 (
    echo [ADVERTENCIA] Algunos tests fallaron
    set /p continue="Continuar con el build? (s/n): "
    if /i not "%continue%"=="s" (
        echo Build cancelado
        pause
        exit /b 1
    )
)
echo [OK] Tests completados
echo.

REM Compilar y empaquetar
echo [5/5] Compilando y empaquetando aplicacion...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Fallo la compilacion
    pause
    exit /b 1
)

echo.
echo ========================================
echo   BUILD EXITOSO
echo ========================================
echo.
echo JAR generado en: target\
dir target\*.jar | findstr /v "sources javadoc"
echo.
echo Siguiente paso:
echo   Ejecutar: .\scripts\deploy-local.bat
echo.
pause
