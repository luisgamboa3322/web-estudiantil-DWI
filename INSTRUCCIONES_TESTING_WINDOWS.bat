@echo off
echo =========================================
echo 🧪 PRUEBA RÁPIDA: Angular + Spring Boot + MySQL
echo =========================================

echo.
echo 1. 🔍 Verificando servicios del sistema...
echo =========================================

:: Verificar si MySQL está corriendo
netstat -an | findstr :3306 >nul
if %errorlevel% equ 0 (
    echo ✅ MySQL está corriendo en puerto 3306
) else (
    echo ❌ MySQL no detectado en puerto 3306
    echo    Ejecuta: net start mysql
)

echo.
echo 2. 🚀 Verificando Spring Boot...
echo =========================================

:: Verificar si Spring Boot está corriendo
netstat -an | findstr :8083 >nul
if %errorlevel% equ 0 (
    echo ✅ Spring Boot está corriendo en puerto 8083
    echo.
    echo Probando endpoints de Spring Boot:
    curl -s http://localhost:8083/api/health >nul
    if %errorlevel% equ 0 (
        echo ✅ API Health respondiendo
    ) else (
        echo ❌ API Health no disponible
    )
) else (
    echo ❌ Spring Boot no detectado en puerto 8083
    echo    Para iniciarlo: cd demo && mvnw spring-boot:run
)

echo.
echo 3. 🎨 Verificando Angular...
echo =========================================

:: Verificar si Angular está corriendo
netstat -an | findstr :4200 >nul
if %errorlevel% equ 0 (
    echo ✅ Angular está corriendo en puerto 4200
) else (
    echo ❌ Angular no detectado en puerto 4200
    echo    Para iniciarlo: cd frontend && npm install && ng serve
)

echo.
echo 4. 📋 COMANDOS PARA TESTING MANUAL
echo =========================================
echo Para probar el sistema manualmente:
echo.
echo 1️⃣ BACKEND (Terminal 1):
echo    cd demo
echo    mvnw spring-boot:run
echo.
echo 2️⃣ FRONTEND (Terminal 2):
echo    cd frontend
echo    npm install
echo    ng serve
echo.
echo 3️⃣ BASE DE DATOS:
echo    mysql -u root -p
echo    USE studyM
echo    SELECT * FROM auth_user
echo.
echo 4️⃣ TEST EN NAVEGADOR:
echo    Abrir: http://localhost:4200
echo    Credenciales: admin@test.com / admin123
echo.
echo 5️⃣ TESTING API:
echo    curl http://localhost:8083/api/health
echo.

echo.
echo 5. 🐛 TROUBLESHOOTING
echo =========================================
echo Si algo no funciona:
echo.
echo ❌ Si Spring Boot no inicia:
echo    - Verificar puertos: netstat -an | findstr :8083
echo    - Verificar application.properties
echo    - Verificar dependencias: mvn clean
echo.
echo ❌ Si Angular no compila:
echo    - Verificar Node.js: node --version
echo    - Reinstalar dependencias: rmdir /s node_modules && npm install
echo    - Verificar TypeScript: ng version
echo.
echo ❌ Si base de datos no conecta:
echo    - Verificar MySQL: net start mysql
echo    - Verificar credenciales en application.properties
echo    - Verificar que la BD 'studyM' existe

echo.
echo =========================================
echo 🏁 INSTRUCCIONES COMPLETAS CREADAS
echo =========================================
echo Consulta GUIA_TESTING_SISTEMA_COMPLETO.md para testing detallado
pause