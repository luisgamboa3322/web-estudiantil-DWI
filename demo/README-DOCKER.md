# 🐳 Guía de Docker - Web Estudiantil

Esta guía explica cómo usar Docker y Docker Compose para ejecutar la aplicación Web Estudiantil de manera containerizada.

## 📋 Requisitos Previos

- **Docker Desktop** instalado y ejecutándose
  - Windows: [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop)
  - Versión mínima: Docker 20.10+
  - Docker Compose viene incluido con Docker Desktop

- **Recursos mínimos recomendados**:
  - RAM: 4GB disponibles
  - Disco: 2GB libres
  - CPU: 2 cores

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno

Copiar el archivo de ejemplo y ajustar valores:

```powershell
# En Windows PowerShell
Copy-Item .env.example .env
```

Editar `.env` con tus credenciales preferidas.

### 2. Construir y Ejecutar

```powershell
# Construir las imágenes y levantar los servicios
docker-compose up -d --build
```

### 3. Verificar Estado

```powershell
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs solo de la aplicación
docker-compose logs -f app

# Ver logs solo de la base de datos
docker-compose logs -f db

# Ver estado de los contenedores
docker-compose ps
```

### 4. Acceder a la Aplicación

Una vez que los servicios estén saludables (healthy):

- **Aplicación Web**: http://localhost:8083
- **MySQL**: localhost:3307 (puerto externo)

## 📦 Servicios Incluidos

### 🗄️ Base de Datos (MySQL 8.0)

- **Contenedor**: `webestudiantil-mysql`
- **Puerto**: 3307:3306 (externo:interno)
- **Volumen**: `webestudiantil_mysql_data` (persistencia de datos)
- **Health Check**: Verifica conectividad cada 10s

### 🌐 Aplicación (Spring Boot)

- **Contenedor**: `webestudiantil-app`
- **Puerto**: 8083:8083
- **Volúmenes**:
  - `webestudiantil_app_logs`: Logs de la aplicación
  - `webestudiantil_app_uploads`: Archivos subidos
- **Health Check**: Verifica endpoint `/actuator/health` cada 30s

## 🛠️ Comandos Útiles

### Gestión de Servicios

```powershell
# Iniciar servicios
docker-compose up -d

# Detener servicios (mantiene datos)
docker-compose stop

# Detener y eliminar contenedores (mantiene volúmenes)
docker-compose down

# Detener y eliminar TODO (incluye volúmenes - ¡CUIDADO!)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart app
docker-compose restart db
```

### Logs y Debugging

```powershell
# Ver logs en tiempo real
docker-compose logs -f

# Ver últimas 100 líneas de logs
docker-compose logs --tail=100

# Ver logs de un servicio específico
docker-compose logs -f app
```

### Reconstruir Imágenes

```powershell
# Reconstruir solo la aplicación
docker-compose build app

# Reconstruir todo desde cero
docker-compose build --no-cache

# Reconstruir y reiniciar
docker-compose up -d --build
```

### Acceso a Contenedores

```powershell
# Acceder a la shell del contenedor de la aplicación
docker exec -it webestudiantil-app sh

# Acceder a MySQL
docker exec -it webestudiantil-mysql mysql -u root -p
```

## 🔧 Configuración Avanzada

### Variables de Entorno Disponibles

Editar archivo `.env`:

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_ROOT_PASSWORD` | Contraseña root de MySQL | `rootpassword` |
| `DB_NAME` | Nombre de la base de datos | `webestudiantil` |
| `DB_USER` | Usuario de la aplicación | `webuser` |
| `DB_PASSWORD` | Contraseña del usuario | `webpassword` |
| `DB_PORT` | Puerto externo de MySQL | `3307` |
| `SPRING_PROFILE` | Perfil de Spring (dev/prod) | `prod` |
| `APP_PORT` | Puerto de la aplicación | `8083` |
| `JAVA_OPTS` | Opciones de JVM | `-Xms512m -Xmx1024m` |

### Cambiar Recursos de Java

Editar `.env`:

```properties
# Para más memoria
JAVA_OPTS=-Xms1g -Xmx2g

# Para menos memoria
JAVA_OPTS=-Xms256m -Xmx512m
```

Luego reiniciar:

```powershell
docker-compose restart app
```

## 📊 Monitoreo

### Health Checks

Los servicios tienen health checks automáticos:

```powershell
# Ver estado de salud
docker-compose ps
```

Estados posibles:
- `starting`: Iniciando
- `healthy`: Funcionando correctamente
- `unhealthy`: Problemas detectados

### Métricas de la Aplicación

Acceder a Spring Boot Actuator:

- Health: http://localhost:8083/actuator/health
- Info: http://localhost:8083/actuator/info
- Metrics: http://localhost:8083/actuator/metrics

## 🔍 Troubleshooting

### Problema: Contenedor no inicia

```powershell
# Ver logs detallados
docker-compose logs app

# Verificar que Docker Desktop está corriendo
docker version
```

### Problema: Puerto ya en uso

```powershell
# Cambiar puerto en .env
APP_PORT=8084
DB_PORT=3308

# Reiniciar
docker-compose down
docker-compose up -d
```

### Problema: Base de datos no conecta

```powershell
# Verificar que el contenedor de DB está healthy
docker-compose ps

# Ver logs de MySQL
docker-compose logs db

# Reiniciar servicio de DB
docker-compose restart db
```

### Problema: Cambios en código no se reflejan

```powershell
# Reconstruir imagen
docker-compose build --no-cache app
docker-compose up -d app
```

### Problema: Sin espacio en disco

```powershell
# Limpiar imágenes no usadas
docker system prune -a

# Ver uso de espacio
docker system df
```

## 🗑️ Limpieza Completa

Para eliminar todo (contenedores, volúmenes, imágenes):

```powershell
# Detener y eliminar contenedores y volúmenes
docker-compose down -v

# Eliminar imágenes de la aplicación
docker rmi webestudiantil-app

# Limpieza completa del sistema Docker
docker system prune -a --volumes
```

> ⚠️ **ADVERTENCIA**: Esto eliminará TODOS los datos. Hacer backup antes.

## 📝 Notas Importantes

1. **Persistencia de Datos**: Los datos de MySQL se guardan en un volumen Docker y persisten entre reinicios
2. **Primer Inicio**: La primera vez puede tardar varios minutos en descargar imágenes y construir
3. **Health Checks**: Esperar a que ambos servicios estén "healthy" antes de usar la aplicación
4. **Logs**: Los logs de la aplicación se guardan en el volumen `webestudiantil_app_logs`
5. **Seguridad**: Cambiar las contraseñas por defecto en producción

## 🔗 Enlaces Útiles

- [Documentación de Docker](https://docs.docker.com/)
- [Documentación de Docker Compose](https://docs.docker.com/compose/)
- [Spring Boot con Docker](https://spring.io/guides/gs/spring-boot-docker/)

## 📞 Soporte

Si encuentras problemas:

1. Revisar logs: `docker-compose logs -f`
2. Verificar health checks: `docker-compose ps`
3. Consultar esta guía de troubleshooting
4. Revisar la documentación oficial de Docker
