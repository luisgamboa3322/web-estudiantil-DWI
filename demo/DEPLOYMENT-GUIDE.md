# 📘 Guía de Despliegue - Web Estudiantil

Esta guía proporciona instrucciones detalladas para desplegar la aplicación Web Estudiantil en un servidor local.

## 📋 Tabla de Contenidos

1. [Requisitos del Sistema](#requisitos-del-sistema)
2. [Configuración Previa](#configuración-previa)
3. [Despliegue Paso a Paso](#despliegue-paso-a-paso)
4. [Verificación Post-Despliegue](#verificación-post-despliegue)
5. [URLs de Acceso](#urls-de-acceso)
6. [Troubleshooting](#troubleshooting)

---

## 📋 Requisitos del Sistema

### Software Requerido

| Software | Versión Mínima | Descarga |
|----------|----------------|----------|
| **Java JDK** | 17+ | [Adoptium](https://adoptium.net/) |
| **Maven** | 3.6+ | [Maven](https://maven.apache.org/download.cgi) |
| **MySQL** | 8.0+ | [MySQL](https://dev.mysql.com/downloads/mysql/) |
| **Git** | 2.0+ | [Git](https://git-scm.com/downloads) |

### Recursos de Hardware

- **CPU**: 2 cores mínimo
- **RAM**: 4GB mínimo (8GB recomendado)
- **Disco**: 2GB libres
- **Red**: Conexión a internet para descargar dependencias

---

## 🔧 Configuración Previa

### 1. Verificar Instalación de Java

```powershell
java -version
```

Debe mostrar Java 17 o superior:
```
openjdk version "17.0.x" ...
```

### 2. Verificar Instalación de Maven

```powershell
mvn --version
```

Debe mostrar Maven 3.6 o superior:
```
Apache Maven 3.x.x
```

### 3. Configurar MySQL

#### Iniciar MySQL

```powershell
# Verificar que MySQL está ejecutándose
Get-Service -Name "MySQL*"

# Si no está iniciado, iniciarlo
Start-Service -Name "MySQL80"  # Ajustar nombre según instalación
```

#### Crear Base de Datos

```sql
-- Conectarse a MySQL
mysql -u root -p

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS webestudiantil CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional, para producción)
CREATE USER 'webuser'@'localhost' IDENTIFIED BY 'tu_password_segura';
GRANT ALL PRIVILEGES ON webestudiantil.* TO 'webuser'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
USE webestudiantil;
```

### 4. Configurar Variables de Entorno (Producción)

Para producción, configurar variables de entorno:

```powershell
# En PowerShell (temporal)
$env:DB_URL = "jdbc:mysql://localhost:3306/webestudiantil"
$env:DB_USERNAME = "webuser"
$env:DB_PASSWORD = "tu_password_segura"
$env:SPRING_PROFILE = "prod"

# Para hacerlo permanente, usar Variables de Entorno del Sistema
```

---

## 🚀 Despliegue Paso a Paso

### Opción A: Despliegue Automatizado (Recomendado)

#### Paso 1: Construir la Aplicación

```powershell
# Navegar al directorio del proyecto
cd c:\Users\Luis\OneDrive\Escritorio\web-estudiantil-clonado-sebas\EstudiaM-s\demo

# Ejecutar script de build
.\scripts\build.bat
```

Este script:
- ✓ Verifica Maven y Java
- ✓ Limpia builds anteriores
- ✓ Ejecuta tests
- ✓ Compila y empaqueta la aplicación
- ✓ Genera el archivo JAR

#### Paso 2: Desplegar la Aplicación

```powershell
# Desplegar en modo desarrollo
.\scripts\deploy-local.bat

# O desplegar en modo producción
.\scripts\deploy-local.bat -Profile prod
```

Este script:
- ✓ Verifica el JAR
- ✓ Verifica Java y MySQL
- ✓ Configura variables de entorno
- ✓ Inicia la aplicación

#### Paso 3: Detener la Aplicación (cuando sea necesario)

```powershell
.\scripts\stop.bat
```

### Opción B: Despliegue Manual

#### Paso 1: Limpiar y Compilar

```powershell
# Limpiar builds anteriores
mvn clean

# Compilar y empaquetar (con tests)
mvn package

# O sin tests (más rápido)
mvn package -DskipTests
```

#### Paso 2: Ejecutar la Aplicación

```powershell
# En modo desarrollo
java -jar target\demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# En modo producción
java -jar target\demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

#### Paso 3: Ejecutar con Opciones de JVM

```powershell
# Con configuración de memoria
java -Xms512m -Xmx1024m -jar target\demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## ✅ Verificación Post-Despliegue

### Checklist de Verificación

- [ ] **Aplicación inicia sin errores**
  - Revisar logs en consola
  - No debe haber excepciones de conexión a BD

- [ ] **Base de datos conectada**
  - Verificar en logs: "HikariPool-1 - Start completed"
  - Tablas creadas automáticamente por Hibernate

- [ ] **Puerto accesible**
  - Acceder a http://localhost:8083
  - No debe mostrar error de conexión

- [ ] **Health check funciona**
  - Acceder a http://localhost:8083/actuator/health
  - Debe mostrar: `{"status":"UP"}`

- [ ] **Páginas principales cargan**
  - Login: http://localhost:8083/login
  - Registro: http://localhost:8083/registro
  - Dashboard: http://localhost:8083/estudiante/dashboard (después de login)

### Pruebas Funcionales

#### 1. Probar Registro de Usuario

```
URL: http://localhost:8083/registro
- Crear un nuevo usuario estudiante
- Verificar que se guarda en la base de datos
```

#### 2. Probar Login

```
URL: http://localhost:8083/login
- Iniciar sesión con el usuario creado
- Verificar redirección al dashboard
```

#### 3. Probar Funcionalidades Principales

- [ ] Dashboard carga correctamente
- [ ] Cursos se muestran
- [ ] Chat funciona
- [ ] Calendario funciona
- [ ] Perfil se puede editar

---

## 🌐 URLs de Acceso

### Aplicación Principal

| Endpoint | URL | Descripción |
|----------|-----|-------------|
| **Inicio** | http://localhost:8083 | Página principal |
| **Login** | http://localhost:8083/login | Inicio de sesión |
| **Registro** | http://localhost:8083/registro | Registro de usuarios |
| **Dashboard Estudiante** | http://localhost:8083/estudiante/dashboard | Panel del estudiante |
| **Dashboard Profesor** | http://localhost:8083/profesor/dashboard | Panel del profesor |

### Actuator (Monitoreo)

| Endpoint | URL | Descripción |
|----------|-----|-------------|
| **Health** | http://localhost:8083/actuator/health | Estado de la aplicación |
| **Info** | http://localhost:8083/actuator/info | Información de la app |
| **Metrics** | http://localhost:8083/actuator/metrics | Métricas de rendimiento |

### Base de Datos

| Parámetro | Valor |
|-----------|-------|
| **Host** | localhost |
| **Puerto** | 3306 |
| **Base de Datos** | webestudiantil |
| **Usuario (dev)** | root |
| **Contraseña (dev)** | luis123 |

---

## 🔍 Troubleshooting

### Problema: "Port 8083 is already in use"

**Solución 1**: Cambiar el puerto

```powershell
# Editar application.properties o usar variable de entorno
$env:SERVER_PORT = 8084
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

**Solución 2**: Detener proceso que usa el puerto

```powershell
# Encontrar proceso
netstat -ano | findstr :8083

# Detener proceso (usar PID del comando anterior)
taskkill /PID <PID> /F
```

### Problema: "Unable to connect to MySQL"

**Causas comunes**:

1. **MySQL no está ejecutándose**
   ```powershell
   # Iniciar MySQL
   Start-Service -Name "MySQL80"
   ```

2. **Credenciales incorrectas**
   - Verificar usuario/contraseña en `application-dev.properties`
   - Verificar que el usuario tiene permisos

3. **Base de datos no existe**
   ```sql
   CREATE DATABASE webestudiantil;
   ```

4. **Puerto incorrecto**
   - Verificar que MySQL está en puerto 3306
   - Verificar firewall

### Problema: "OutOfMemoryError"

**Solución**: Aumentar memoria de JVM

```powershell
java -Xms1g -Xmx2g -jar target\demo-0.0.1-SNAPSHOT.jar
```

### Problema: "ClassNotFoundException" o "NoSuchMethodError"

**Solución**: Limpiar y reconstruir

```powershell
mvn clean install -U
```

### Problema: Cambios en código no se reflejan

**Solución**:

```powershell
# Detener aplicación (Ctrl+C)
# Reconstruir
mvn clean package -DskipTests
# Reiniciar
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

### Problema: "Whitelabel Error Page"

**Causas comunes**:
- Ruta incorrecta
- Controlador no mapeado
- Template Thymeleaf no encontrado

**Solución**: Revisar logs para ver el error específico

### Ver Logs Detallados

```powershell
# Ejecutar con logging debug
java -jar target\demo-0.0.1-SNAPSHOT.jar --logging.level.root=DEBUG
```

---

## 📊 Monitoreo y Logs

### Ubicación de Logs

- **Consola**: Logs en tiempo real durante ejecución
- **Archivo** (producción): `logs/webestudiantil.log`
- **Errores** (producción): `logs/webestudiantil-error.log`

### Ver Logs en Tiempo Real

```powershell
# En Windows PowerShell
Get-Content logs\webestudiantil.log -Wait -Tail 50
```

### Niveles de Log por Perfil

**Desarrollo (`dev`)**:
- Nivel: DEBUG
- SQL queries visibles
- Stack traces completos

**Producción (`prod`)**:
- Nivel: INFO/WARN
- SQL queries ocultos
- Stack traces ocultos

---

## 🔐 Credenciales por Defecto

### Desarrollo

| Tipo | Usuario | Contraseña |
|------|---------|------------|
| Base de Datos | root | luis123 |
| Aplicación | (crear en registro) | - |

### Producción

> ⚠️ **IMPORTANTE**: Cambiar TODAS las credenciales en producción

- Usar variables de entorno
- No hardcodear contraseñas
- Usar contraseñas seguras (12+ caracteres)

---

## 📝 Notas Adicionales

### Perfiles de Spring

La aplicación soporta dos perfiles:

1. **dev** (desarrollo):
   - Caché deshabilitado
   - Logs verbosos
   - Hot reload
   - Credenciales en archivo

2. **prod** (producción):
   - Caché habilitado
   - Logs optimizados
   - Variables de entorno
   - Optimizaciones de rendimiento

### Cambiar entre Perfiles

```powershell
# Desarrollo
java -jar app.jar --spring.profiles.active=dev

# Producción
java -jar app.jar --spring.profiles.active=prod
```

### Backup de Base de Datos

```powershell
# Exportar base de datos
mysqldump -u root -p webestudiantil > backup_webestudiantil.sql

# Importar base de datos
mysql -u root -p webestudiantil < backup_webestudiantil.sql
```

---

## 📞 Soporte

Si encuentras problemas:

1. ✅ Revisar esta guía de troubleshooting
2. ✅ Verificar logs de la aplicación
3. ✅ Verificar logs de MySQL
4. ✅ Consultar documentación de Spring Boot
5. ✅ Revisar issues en el repositorio

---

## 🔗 Enlaces Útiles

- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de MySQL](https://dev.mysql.com/doc/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

---

**Última actualización**: Noviembre 2025  
**Versión de la aplicación**: 1.0  
**Versión de Spring Boot**: 3.x
