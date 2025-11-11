#!/bin/bash

echo "========================================="
echo "🧪 TESTING: API Endpoints de Spring Boot"
echo "========================================="

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "1. 🔍 Verificando endpoints básicos..."
echo "========================================="

# Test API Health
echo "Probando /api/health..."
response=$(curl -s -w "%{http_code}" http://localhost:8083/api/health)
http_code="${response: -3}"
body="${response%???}"

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✅ /api/health: HTTP $http_code - $body${NC}"
else
    echo -e "${RED}❌ /api/health: HTTP $http_code${NC}"
fi

# Test Auth Test
echo "Probando /api/auth/test..."
response=$(curl -s -w "%{http_code}" http://localhost:8083/api/auth/test)
http_code="${response: -3}"
body="${response%???}"

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✅ /api/auth/test: HTTP $http_code - $body${NC}"
else
    echo -e "${RED}❌ /api/auth/test: HTTP $http_code${NC}"
fi

echo ""
echo "2. 🚀 Probando Login (Admin)..."
echo "========================================="

# Test login con admin
response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}')

http_code=$(echo "$response" | tail -1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✅ Login exitoso: HTTP $http_code${NC}"
    echo "Response: $body" | head -c 200
    echo "..."
    
    # Extraer cookie de sesión si está presente
    session_cookie=$(echo "$response" | grep -i 'set-cookie' | cut -d: -f2 | cut -d; -f1)
    if [ ! -z "$session_cookie" ]; then
        echo ""
        echo "Cookie de sesión encontrada: $session_cookie"
        SESSION_COOKIE="$session_cookie"
    fi
else
    echo -e "${RED}❌ Login falló: HTTP $http_code${NC}"
    echo "Response: $body"
fi

echo ""
echo "3. 📊 Probando Dashboard API..."
echo "========================================="

# Test dashboard
if [ ! -z "$SESSION_COOKIE" ]; then
    echo "Probando /api/dashboard con cookie de sesión..."
    response=$(curl -s -w "\n%{http_code}" http://localhost:8083/api/dashboard \
      -H "Cookie: $SESSION_COOKIE")
    
    http_code=$(echo "$response" | tail -1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ /api/dashboard: HTTP $http_code${NC}"
        echo "Datos recibidos:"
        echo "$body" | head -c 300
        echo "..."
    else
        echo -e "${RED}❌ /api/dashboard: HTTP $http_code${NC}"
        echo "Response: $body"
    fi
else
    echo -e "${YELLOW}⚠️ Omitiendo /api/dashboard - no hay cookie de sesión${NC}"
fi

echo ""
echo "4. 👥 Probando endpoints de datos..."
echo "========================================="

if [ ! -z "$SESSION_COOKIE" ]; then
    # Test students
    response=$(curl -s -w "\n%{http_code}" http://localhost:8083/api/students \
      -H "Cookie: $SESSION_COOKIE")
    http_code=$(echo "$response" | tail -1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ /api/students: HTTP $http_code${NC}"
    else
        echo -e "${RED}❌ /api/students: HTTP $http_code${NC}"
    fi
    
    # Test professors
    response=$(curl -s -w "\n%{http_code}" http://localhost:8083/api/professors \
      -H "Cookie: $SESSION_COOKIE")
    http_code=$(echo "$response" | tail -1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ /api/professors: HTTP $http_code${NC}"
    else
        echo -e "${RED}❌ /api/professors: HTTP $http_code${NC}"
    fi
    
    # Test cursos
    response=$(curl -s -w "\n%{http_code}" http://localhost:8083/api/cursos \
      -H "Cookie: $SESSION_COOKIE")
    http_code=$(echo "$response" | tail -1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ /api/cursos: HTTP $http_code${NC}"
    else
        echo -e "${RED}❌ /api/cursos: HTTP $http_code${NC}"
    fi
else
    echo -e "${YELLOW}⚠️ Omitiendo endpoints de datos - no hay cookie de sesión${NC}"
fi

echo ""
echo "5. 🔓 Probando Logout..."
echo "========================================="

if [ ! -z "$SESSION_COOKIE" ]; then
    response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8083/api/auth/logout \
      -H "Cookie: $SESSION_COOKIE")
    http_code=$(echo "$response" | tail -1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ Logout: HTTP $http_code${NC}"
    else
        echo -e "${RED}❌ Logout: HTTP $http_code${NC}"
    fi
fi

echo ""
echo "========================================="
echo "📋 RESUMEN"
echo "========================================="

echo "Si todos los endpoints devolvieron HTTP 200:"
echo -e "${GREEN}✅ ¡Tu API está funcionando correctamente!${NC}"
echo ""
echo "Ahora puedes probar en el navegador:"
echo "- Ir a: http://localhost:4200"
echo "- Login: admin@test.com / admin123"
echo "- Debería cargar datos del dashboard"
echo ""
echo "Si hay errores:"
echo "- Verificar que Spring Boot esté ejecutándose en puerto 8083"
echo "- Verificar que MySQL esté corriendo"
echo "- Revisar logs de Spring Boot para errores específicos"

echo ""
echo "========================================="
echo "🎯 PRÓXIMOS PASOS"
echo "========================================="
echo "1. Si los endpoints API funcionan:"
echo "   - El problema está en Angular frontend"
echo "   - Revisar console del navegador (F12)"
echo "   - Verificar proxy.conf.json"
echo ""
echo "2. Si los endpoints API NO funcionan:"
echo "   - Reiniciar Spring Boot"
echo "   - Verificar configuration de CORS"
echo "   - Revisar que la base de datos tenga datos"