#!/bin/bash

# Script de prueba rápida del sistema completo
echo "========================================="
echo "🧪 PRUEBA RÁPIDA: Angular + Spring Boot + MySQL"
echo "========================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar si un puerto está en uso
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${GREEN}✅ $service está corriendo en puerto $port${NC}"
        return 0
    else
        echo -e "${RED}❌ $service NO está corriendo en puerto $port${NC}"
        return 1
    fi
}

# Función para verificar endpoints
check_endpoint() {
    local url=$1
    local description=$2
    local response=$(curl -s -o /dev/null -w "%{http_code}" $url 2>/dev/null)
    if [ "$response" -eq 200 ] || [ "$response" -eq 404 ]; then
        echo -e "${GREEN}✅ $description respondiendo (HTTP $response)${NC}"
        return 0
    else
        echo -e "${RED}❌ $description NO disponible (HTTP $response)${NC}"
        return 1
    fi
}

echo "1. 🔍 Verificando servicios del sistema..."
echo "========================================="

# Verificar MySQL
echo "Verificando MySQL..."
if systemctl is-active --quiet mysql; then
    echo -e "${GREEN}✅ MySQL está corriendo${NC}"
else
    echo -e "${YELLOW}⚠️ MySQL no detectado con systemctl, probando con lsof...${NC}"
    if lsof -i :3306 >/dev/null 2>&1; then
        echo -e "${GREEN}✅ MySQL puerto 3306 en uso${NC}"
    else
        echo -e "${RED}❌ MySQL no detectado${NC}"
    fi
fi

echo ""
echo "2. 🚀 Verificando Spring Boot..."
echo "========================================="

# Verificar Spring Boot (puerto 8083)
check_port 8083 "Spring Boot"

# Verificar endpoints de Spring Boot
if check_port 8083 "Spring Boot" >/dev/null; then
    echo "Probando endpoints de Spring Boot:"
    check_endpoint "http://localhost:8083/" "Spring Boot Root"
    check_endpoint "http://localhost:8083/api/auth/test" "API Auth Test"
    check_endpoint "http://localhost:8083/api/health" "API Health"
else
    echo -e "${YELLOW}⚠️ Saltando tests de endpoints - Spring Boot no disponible${NC}"
fi

echo ""
echo "3. 🎨 Verificando Angular..."
echo "========================================="

# Verificar Angular (puerto 4200)
check_port 4200 "Angular Dev Server"

# Verificar aplicación Angular
if check_port 4200 "Angular" >/dev/null; then
    check_endpoint "http://localhost:4200/" "Angular App"
else
    echo -e "${YELLOW}⚠️ Saltando tests de Angular - Frontend no disponible${NC}"
fi

echo ""
echo "4. 🌐 Verificando conectividad entre servicios..."
echo "========================================="

if check_port 8083 "Spring Boot" >/dev/null && check_port 4200 "Angular" >/dev/null; then
    echo -e "${GREEN}✅ Ambos servicios están disponibles para testing${NC}"
    echo ""
    echo "🔗 PRUEBAS DE CONECTIVIDAD:"
    echo "- Frontend → Backend: http://localhost:4200 → http://localhost:8083"
    echo "- API URL: http://localhost:8083/api"
    echo "- CORS configurado para: http://localhost:4200"
else
    echo -e "${RED}❌ No se puede realizar testing de conectividad${NC}"
fi

echo ""
echo "5. 📋 RESUMEN DE COMANDOS PARA TESTING MANUAL"
echo "========================================="
echo "Para probar el sistema manualmente:"
echo ""
echo "1️⃣ BACKEND:"
echo "   cd EstudiaM-s/demo/"
echo "   ./mvnw spring-boot:run"
echo ""
echo "2️⃣ FRONTEND:"
echo "   cd EstudiaM-s/frontend/"
echo "   npm install"
echo "   ng serve"
echo ""
echo "3️⃣ BASE DE DATOS:"
echo "   mysql -u root -p"
echo "   USE studyM;"
echo "   SELECT * FROM auth_user;"
echo ""
echo "4️⃣ TEST EN NAVEGADOR:"
echo "   Abrir: http://localhost:4200"
echo "   Credenciales: admin@test.com / admin123"
echo ""
echo "5️⃣ TESTING API:"
echo "   curl http://localhost:8083/api/health"
echo "   curl -X POST http://localhost:8083/api/auth/login -H 'Content-Type: application/json' -d '{\"email\":\"admin@test.com\",\"password\":\"admin123\"}'"

echo ""
echo "6. 🐛 TROUBLESHOOTING RÁPIDO"
echo "========================================="
echo "Si algo no funciona:"
echo ""
echo "❌ Si Spring Boot no inicia:"
echo "   - Verificar puertos: lsof -i :8083"
echo "   - Verificar application.properties"
echo "   - Verificar dependencias: mvn clean"
echo ""
echo "❌ Si Angular no compila:"
echo "   - Verificar Node.js: node --version"
echo "   - Reinstalar dependencias: rm -rf node_modules && npm install"
echo "   - Verificar TypeScript: ng version"
echo ""
echo "❌ Si base de datos no conecta:"
echo "   - Verificar MySQL: systemctl status mysql"
echo "   - Verificar credenciales en application.properties"
echo "   - Verificar que la BD 'studyM' existe"

echo ""
echo "========================================="
echo "🏁 TESTING COMPLETO"
echo "========================================="
echo "Consulta GUIA_TESTING_SISTEMA_COMPLETO.md para testing detallado"