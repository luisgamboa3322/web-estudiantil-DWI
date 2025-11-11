# 🧪 GUÍA DE TESTING: Sistema Completo Angular + Spring Boot + MySQL

## 📋 CHECKLIST DE VERIFICACIÓN

### 1. 🔍 VERIFICAR INFRAESTRUCTURA BASE

#### 1.1 Base de Datos MySQL
```bash
# Verificar que MySQL esté corriendo
sudo systemctl status mysql
# o en Windows:
net start mysql

# Verificar conexión a la BD
mysql -u root -p
# Dentro de MySQL:
SHOW DATABASES;
USE studyM;
SHOW TABLES;
```

#### 1.2 Verificar Datos de Prueba
```sql
-- En MySQL, verificar que existan datos:
SELECT * FROM admin LIMIT 1;
SELECT * FROM professor LIMIT 1;
SELECT * FROM student LIMIT 1;
SELECT * FROM curso LIMIT 1;

-- Verificar usuarios con roles
SELECT username, password, roles FROM auth_user;
```

### 2. 🚀 VERIFICAR SPRING BOOT

#### 2.1 Ejecutar Backend
```bash
# Navegar al directorio del backend
cd EstudiaM-s/demo/

# Ejecutar Spring Boot
./mvnw spring-boot:run
# o en Windows:
mvnw.cmd spring-boot:run
```

#### 2.2 Verificar Endpoints Base
```bash
# Verificar que el servidor esté corriendo
curl http://localhost:8083/
# Debe retornar algo como: "OK" o error 404

# Verificar salud del sistema
curl http://localhost:8083/api/health
curl http://localhost:8083/api/test
```

#### 2.3 Verificar Endpoints de Autenticación
```bash
# Test de login
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "admin123"
  }'

# Test de endpoints protegidos (necesitará token)
curl -X GET http://localhost:8083/api/auth/me \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

### 3. 🎨 VERIFICAR FRONTEND ANGULAR

#### 3.1 Instalar Dependencias
```bash
cd EstudiaM-s/frontend/
npm install
```

#### 3.2 Ejecutar Frontend
```bash
# Ejecutar Angular en modo desarrollo
npm run start
# o
ng serve

# Angular debería estar disponible en: http://localhost:4200
```

#### 3.3 Verificar Proxy Configuration
```bash
# Verificar que proxy.conf.json esté configurado correctamente
cat proxy.conf.json
# Debe apuntar a http://localhost:8083
```

### 4. 🔄 PRUEBAS DE INTEGRACIÓN

#### 4.1 Flujo de Login Completo

1. **Abrir navegador**: http://localhost:4200
2. **Probar Login**:
   - Usar credenciales: `admin@test.com` / `admin123`
   - Verificar redirección a `/select-dashboard`
   - Verificar que no aparezcan errores en consola

#### 4.2 Probar Dashboard de Administrador

1. **En Select Dashboard**: Seleccionar "Dashboard Administrador"
2. **Verificar datos**: Debe cargar lista de estudiantes, profesores, cursos
3. **Probar acciones**:
   - Click en "Añadir Estudiante" (debe abrir modal)
   - Buscar estudiante (debe filtrar tabla)
   - Verificar que sidebar funcione correctamente

#### 4.3 Probar Navegación

1. **Sidebar de profesor**:
   - Ir a `/profesor/dashboard`
   - Verificar que cargue lista de cursos
   - Navegar a `/profesor/calendario`
   - Navegar a `/profesor/configuracion`

2. **Sidebar de estudiante**:
   - Ir a `/student/dashboard`
   - Verificar que cargue cursos asignados
   - Navegar a `/student/calendario`

### 5. 🔧 COMANDOS DE VERIFICACIÓN RÁPIDA

#### 5.1 Script de Health Check
```bash
# Crear script health-check.sh
cat > health-check.sh << 'EOF'
#!/bin/bash
echo "=== HEALTH CHECK DEL SISTEMA ==="

echo "1. Verificando MySQL..."
sudo systemctl is-active mysql
if [ $? -eq 0 ]; then
    echo "✅ MySQL está corriendo"
else
    echo "❌ MySQL NO está corriendo"
fi

echo "2. Verificando Spring Boot..."
curl -s http://localhost:8083/api/health > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Spring Boot está corriendo en puerto 8083"
else
    echo "❌ Spring Boot NO está corriendo en puerto 8083"
fi

echo "3. Verificando Angular..."
curl -s http://localhost:4200 > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Angular está corriendo en puerto 4200"
else
    echo "❌ Angular NO está corriendo en puerto 4200"
fi

echo "4. Verificando conexión Backend-Frontend..."
curl -s http://localhost:4200 > /dev/null
echo "   Si tanto Backend como Frontend están corriendo, deben poder comunicarse"

echo "=== FIN HEALTH CHECK ==="
EOF

chmod +x health-check.sh
./health-check.sh
```

#### 5.2 Test de API desde Frontend
```javascript
// En la consola del navegador (F12)
fetch('http://localhost:8083/api/health')
  .then(response => response.text())
  .then(data => console.log('Backend Response:', data))
  .catch(error => console.error('Error:', error));
```

### 6. 🐛 TROUBLESHOOTING COMÚN

#### 6.1 Error: "CORS" en navegador
**Solución**: Verificar WebConfig.java en Spring Boot
```java
@CrossOrigin(origins = "http://localhost:4200")
```

#### 6.2 Error: "No se puede conectar con backend"
**Solución**:
- Verificar que proxy.conf.json esté configurado
- Verificar que CORS esté habilitado en Spring Boot
- Verificar que puertos no estén bloqueados

#### 6.3 Error: "Base de datos no se conecta"
**Solución**:
- Verificar application.properties
- Verificar que MySQL esté corriendo
- Verificar credenciales de BD

#### 6.4 Error: "Componentes de Angular no se encuentran"
**Solución**:
```bash
cd frontend/
npm install
ng build
ng serve
```

### 7. 📊 TESTING DE PERFORMANCE

#### 7.1 Carga de Datos
```bash
# En navegador, abrir Developer Tools (F12)
# Ir a Network tab
# Recargar página
# Verificar tiempos de respuesta < 2 segundos
```

#### 7.2 Memory Usage
```bash
# En Developer Tools
# Ir a Memory tab
# Tomar heap snapshot
# Verificar memory leaks
```

### 8. ✅ CRITERIOS DE ÉXITO

**✅ SISTEMA FUNCIONANDO CORRECTAMENTE SI:**

1. **MySQL**: Conectado y con datos
2. **Spring Boot**: Ejecutándose en puerto 8083, endpoints respondiendo
3. **Angular**: Ejecutándose en puerto 4200, sin errores de compilación
4. **Login**: Función correctamente y redirige según el rol
5. **Dashboard Admin**: Carga y muestra datos reales de la BD
6. **Navegación**: Todos los enlaces funcionan sin errores 404
7. **Responsive**: Funciona en móvil y desktop
8. **Consola**: Sin errores rojos en Developer Tools

### 9. 🎯 FLUJO DE TESTING RECOMENDADO

1. **Ejecutar health-check.sh**
2. **Probar login en navegador**
3. **Verificar dashboard admin**
4. **Probar navegación profesor/estudiante**
5. **Verificar que datos se cargan desde MySQL**
6. **Probar funcionalidades CRUD (crear, editar, eliminar)**
7. **Verificar responsive design**
8. **Probar logout**

### 10. 📞 CONTACTOS DE EMERGENCIA

Si algo no funciona:
1. Revisar logs de Spring Boot (consola donde se ejecutó)
2. Revisar console del navegador (F12)
3. Verificar puertos no estén en uso: `netstat -an | grep :8083`
4. Reiniciar servicios: MySQL → Spring Boot → Angular