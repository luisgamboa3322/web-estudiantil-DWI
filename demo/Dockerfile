# ============================================
# Multi-stage Dockerfile para Web Estudiantil
# ============================================

# Etapa 1: Build - Compilar la aplicación
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar caché de Docker)
COPY pom.xml .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (saltar tests para build más rápido)
# Para incluir tests, cambiar a: mvn clean package -B
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime - Ejecutar la aplicación
FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="Web Estudiantil Team"
LABEL description="Aplicación Web Estudiantil - Spring Boot"
LABEL version="1.0"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto de la aplicación
EXPOSE 8083

# Variables de entorno por defecto
ENV SPRING_PROFILE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILE -jar app.jar"]
