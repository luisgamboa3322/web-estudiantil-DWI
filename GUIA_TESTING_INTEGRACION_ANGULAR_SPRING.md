# 🔧 GUÍA DE TESTING - INTEGRACIÓN ANGULAR + SPRING BOOT

## ✅ Estado Actual
**BUILD ANGULAR EXITOSO** - La migración está completa y funcionando.

## 🚀 Pasos para Probar la Integración Completa

### 1. Iniciar Backend (Spring Boot + MySQL)
```bash
cd EstudiaM-s/demo
./mvnw spring-boot:run
# O en Windows:
mvnw.cmd spring-boot:run
```

**Verificar que el backend esté corriendo:**
- URL de prueba: http://localhost:8083/api/auth/test
- Debería mostrar: "Auth API funcionando correctamente"

### 2. Iniciar Frontend (Angular)
```bash
cd EstudiaM-s/frontend
ng serve -o
```

**El navegador debería abrir automáticamente en:** http://localhost:4200

### 3. Flujo de Testing Completo

#### 🔐 **Test de Autenticación**

1. **Acceder al Login:**
   - URL: http://localhost:4200/login
   - Verificar que la página carga correctamente

2. **Credenciales de Prueba:**
   - **Administrador:** admin@utp.edu.pe / admin123
   - **Profesor:** prof@utp.edu.pe / prof123  
   - **Estudiante:** est@utp.edu.pe / est123

3. **Verificar Login:**
   - Al ingresar credenciales válidas debería redirigir a `/select-dashboard`
   - Abrir DevTools (F12) → Console para ver logs de debug

#### 🎯 **Test de Navegación por Roles**

1. **Login como Admin:**
   - Redirige a `/select-dashboard`
   - Selecciona "Dashboard Administrador"
   - Verifica acceso a `/admin/dashboard`

2. **Login como Profesor:**
   - Redirige a `/select-dashboard` 
   - Selecciona "Dashboard Docente"
   - Verifica acceso a `/profesor/dashboard`

3. **Login como Estudiante:**
   - Redirige a `/select-dashboard`
   - Selecciona "Dashboard Estudiante"
   - Verifica acceso a `/student/dashboard`

#### 🔍 **Verificar DevTools (F12)**

**En Console deberías ver logs como:**
```
Login response: {success: true, email: "admin@utp.edu.pe", role: "ROLE_ADMIN", ...}
Setting user from login response: {email: "admin@utp.edu.pe", userType: "ADMIN", ...}
```

**En Network deberías ver requests a:**
- `http://localhost:8083/api/auth/login`
- `http://localhost:8083/api/auth/me`
- `http://localhost:8083/api/auth/check`

### 4. ⚠️ **Problemas Comunes y Soluciones**

#### **CORS Errors:**
```bash
# Verificar que WebConfig.java esté configurado para CORS
# Should allow: http://localhost:4200
```

#### **Connection Refused:**
```bash
# Verificar que MySQL esté ejecutándose
# Verificar credenciales en application.properties
```

#### **401 Unauthorized:**
```bash
# Verificar que el backend esté corriendo en puerto 8083
# Revisar logs del AuthService en console del navegador
```

### 5. 🧪 **Tests Específicos por Módulo**

#### **Admin Dashboard:**
- ✅ Cargar lista de estudiantes
- ✅ Cargar lista de profesores
- ✅ Cargar lista de cursos
- ✅ Botones de CRUD funcionales

#### **Profesor Dashboard:**
- ✅ Ver cursos asignados
- ✅ Navegación a gestión de curso
- ✅ Sidebar y header funcionando

#### **Student Dashboard:**
- ✅ Ver cursos matriculados
- ✅ Actividades semanales
- ✅ Sistema de notificaciones

### 6. 📊 **Logs Importantes**

**En Angular Console:**
```javascript
// Login exitoso
Login response: {success: true, ...}
Setting user from login response: {email: "user@utp.edu.pe", userType: "ADMIN", ...}

// Verificación de sesión
Session check successful, authenticated: true

// Navegación
Navigating to /admin/dashboard
```

**En Spring Boot Console:**
```java
// Login request received
POST /api/auth/login - 200 OK

// Session verification
GET /api/auth/check - 200 OK

// User info retrieval  
GET /api/auth/me - 200 OK
```

### 7. 🔧 **Comandos Útiles**

```bash
# Verificar puertos en uso
netstat -an | grep 8083
netstat -an | grep 4200

# Verificar estado MySQL
mysql -u root -p -e "SHOW DATABASES;"

# Logs detallados de Spring Boot
./mvnw spring-boot:run --debug

# Rebuild Angular con cache limpio
cd frontend && rm -rf .angular && ng build
```

### 8. ✅ **Checklist de Funcionalidades**

- [ ] **Login exitoso** con credenciales válidas
- [ ] **Logout funcional**
- [ ] **Navegación** entre componentes
- [ ] **Sesión persistente** (refresh de página)
- [ ] **Roles y permisos** correctos
- [ ] **APIs del backend** respondiendo
- [ ] **CORS configurado** correctamente
- [ ] **Error handling** funcionando
- [ ] **Responsive design** en diferentes tamaños
- [ ] **Iconos y estilos** cargando correctamente

### 9. 🚨 **Si Algo No Funciona**

1. **Verificar que ambos servidores estén corriendo**
2. **Revisar consola del navegador (F12)**
3. **Verificar Network tab para requests**
4. **Revisar logs de Spring Boot**
5. **Confirmar credenciales de MySQL**
6. **Verificar configuración de CORS**

## 📈 **Próximos Pasos Post-Testing**

Si todo funciona correctamente:
1. ✅ Documentar cualquier bug encontrado
2. 🔄 Optimizar performance
3. 📱 Probar en diferentes navegadores
4. 🧪 Agregar tests automatizados
5. 🚀 Preparar para producción

---

**¡El sistema está listo para testing completo!** 🎉