# Sistema Web Estudiantil (Spring Boot)

Aplicación web con autenticación por formulario, vistas con Thymeleaf y endpoints JSON para gestionar Estudiantes y Profesores. La redirección posterior al login se hace según la inicial del código del usuario.

Tecnologías
- Java 21
- Spring Boot 3.5.5
- Spring Web, Spring Security, Spring Data JPA
- MySQL
- Thymeleaf
- Maven

Indice
- Requisitos
- Configuración (MySQL y application.properties)
- Datos iniciales
- Ejecución
- Seguridad y autenticación
- Endpoints (vistas y API)
- Pruebas con curl (paso a paso)
- Swagger/OpenAPI (opcional)
- Estructura del proyecto
- Problemas comunes y notas

Requisitos
- Java 17 (JDK)
- Maven (o usar el wrapper mvnw/mvnw.cmd incluido)
- MySQL en localhost 
- Puerto 8083 libre (configurable)

Configuración
Archivo de propiedades: src/main/resources/application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/webestudiantil?useSSL=false&amp;serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=luis123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.cache=false

# Puerto del servidor
server.port=8083

Base de datos
1) Crear la base de datos si no existe:
CREATE DATABASE webestudiantil CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

2) Ajustar usuario/clave en application.properties si difieren de tu entorno.

Datos iniciales
Al iniciar la app, se crea automáticamente un usuario de tipo Student con código de admin (para redirigir a /admin):
- email: admin@example.com
- password: admin123
- codigo: A0001

Esto se hace en com.example.demo.config.DataInitializer.

Ejecución
Con Maven Wrapper (Windows):
mvnw.cmd spring-boot:run

Con Maven instalado:
mvn spring-boot:run

Empaquetado y ejecución por JAR:
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar

Aplicación en: http://localhost:8083

Seguridad y autenticación
- Login por formulario:
  - Página: GET /login (también GET / devuelve login)
  - Procesamiento: POST /login
  - Campos: email, password
- Logout:
  - POST /logout (Spring Security)
  - Existe además GET /logout (controlador) que invalida la sesión y redirige a /login?logout=true
- CSRF:
  - Deshabilitado (http.csrf().disable()). No se requiere token CSRF para POST/PUT/DELETE.
- Rutas públicas:
  - /, /login
  - estáticos: /css/**, /js/**, /images/**, /webjars/**, /static/**, /favicon.ico, /error
  - /admin/students (ver nota de seguridad)
- Redirección tras login (AuthenticationSuccessHandler):
  - Se busca Student por email; según la inicial de su propiedad codigo:
    - 'U' → /student/dashboard
    - 'C' → /teacher/dashboard
    - 'A' → /admin/dashboard
  - Si no hay codigo, usa la primera letra del email como fallback (misma lógica).
- Roles:
  - CustomUserDetailsService asigna roles STUDENT o TEACHER, pero actualmente no se usan en las reglas de autorización (se autentica todo lo no público sin revisar roles).
- Nota de seguridad:
  - /admin/students está configurado como público en SecurityConfig. Cualquiera podría crear estudiantes. Ajusta según tu necesidad.

Endpoints

Vistas (Thymeleaf)
- GET /login
  - Vista: templates/login.html
- GET /
  - Devuelve la misma vista de login.
- GET /admin/dashboard (autenticado)
  - Vista: templates/administrador/dashboard.html
  - Model: students, professors
- GET /teacher/dashboard (autenticado)
  - Vista: templates/profesor/dashboard.html
- GET /student/dashboard (autenticado)
  - Vista: templates/student/dashboard.html
  - Model: studentName, activePage
- GET /student/chat (autenticado)
  - Vista: templates/student/chat.html
- GET /student/calendario (autenticado)
  - Vista: templates/student/calendario.html
- GET /student/configuracion (autenticado)
  - Vista: templates/student/configuracion.html

API JSON

AdminController (/admin)
- POST /admin/students  [PÚBLICO]
  - Crea un Student. La contraseña se cifra con BCrypt antes de guardar.
  - Body JSON:
    {
      "nombre": "Juan Pérez",
      "codigo": "U2025001",
      "email": "juan@example.com",
      "password": "123456"
    }
  - Respuesta: 200 OK con Student creado.
- POST /admin/profesores  [AUTENTICADO]
  - Crea un Professor (valida unicidad de codigo y email).
  - Body JSON:
    {
      "nombre": "Carlos",
      "codigo": "C2025001",
      "email": "carlos@example.com",
      "password": "secreto",
      "especialidad": "Matemáticas"
    }
  - Respuesta: 200 OK con Professor creado.

ProfesorController (/teacher)
- GET /teacher  [AUTENTICADO]
  - Devuelve lista de profesores.
  - Nota: Este método devuelve List<Professor> en un @Controller sin @ResponseBody; según configuración puede ser necesario anotar @ResponseBody o usar @RestController para garantizar JSON.
- GET /teacher/{id}  [AUTENTICADO]
  - Devuelve 200 OK con Professor o 404 Not Found si no existe.
- POST /teacher  [AUTENTICADO]
  - Crea un profesor (misma validación de unicidad). Contraseña cifrada.
  - Body JSON:
    {
      "nombre": "Ana",
      "codigo": "C2025002",
      "email": "ana@example.com",
      "password": "clave",
      "especialidad": "Física"
    }
  - Respuesta: 200 OK con Professor creado.
- PUT /teacher/{id}  [AUTENTICADO]
  - Actualiza campos enviados. Si cambia email/codigo, valida unicidad. Si cambia password, se vuelve a cifrar.
  - Body JSON: campos a modificar.
  - Respuesta: 200 OK con Professor actualizado.
- DELETE /teacher/{id}  [AUTENTICADO]
  - Elimina un profesor. Respuesta: 204 No Content.

LoginController
- GET /logout
  - Invalida sesión y redirige a /login?logout=true (además del POST /logout de Security).

Pruebas con curl

1) Login como admin y guardar sesión (cookies)
curl -i -c cookies.txt -X POST "http://localhost:8083/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data "email=admin@example.com&amp;password=admin123" -L

2) Crear Student (ruta pública, no requiere sesión)
curl -i -X POST "http://localhost:8083/admin/students" \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Juan Pérez\",\"codigo\":\"U2025001\",\"email\":\"juan@example.com\",\"password\":\"123456\"}"

3) Crear Professor (requiere sesión autenticada)
curl -i -b cookies.txt -X POST "http://localhost:8083/admin/profesores" \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Carlos\",\"codigo\":\"C2025001\",\"email\":\"carlos@example.com\",\"password\":\"secreto\",\"especialidad\":\"Matemáticas\"}"

4) Listar profesores (requiere sesión)
curl -i -b cookies.txt "http://localhost:8083/teacher"

5) Obtener un profesor por id (requiere sesión)
curl -i -b cookies.txt "http://localhost:8083/teacher/1"

6) Actualizar un profesor (requiere sesión)
curl -i -b cookies.txt -X PUT "http://localhost:8083/teacher/1" \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Carlos Actualizado\",\"especialidad\":\"Álgebra\"}"

7) Eliminar un profesor (requiere sesión)
curl -i -b cookies.txt -X DELETE "http://localhost:8083/teacher/1"

8) Probar dashboards en navegador
- http://localhost:8083/login (login)
- Luego según el código del usuario: /admin/dashboard, /teacher/dashboard, /student/dashboard

Swagger/OpenAPI (opcional)
No está incluido por defecto. Para habilitar Swagger UI:
1) Agregar dependencia en pom.xml dentro de <dependencies>:
   <dependency>
     <groupId>org.springdoc</groupId>
     <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
     <version>2.6.0</version>
   </dependency>
2) Levantar la app y abrir:
   - Swagger UI: http://localhost:8083/swagger-ui.html
   - OpenAPI JSON: http://localhost:8083/v3/api-docs

Estructura del proyecto (parcial)
- src/main/java/com/example/demo
  - DemoApplication.java
  - config/
    - SecurityConfig.java (seguridad)
    - DataInitializer.java (usuario admin inicial)
  - controller/
    - LoginController.java
    - AdminController.java
    - ProfesorController.java
    - StudentController.java
  - model/
    - Student.java
    - Professor.java
  - repository/
    - StudentRepository.java
    - ProfessorRepository.java
  - service/
    - CustomUserDetailsService.java
    - StudentService.java
    - ProfessorService.java
  - util/
    - BcryptGen.java
- src/main/resources/
  - application.properties
  - templates/
    - login.html
    - administrador/dashboard.html
    - profesor/dashboard.html
    - student/ (dashboard.html, chat.html, calendario.html, configuracion.html)
  - static/ (estáticos)
- pom.xml

Problemas comunes y notas
- 401/302 al llamar endpoints autenticados por curl:
  - Debes iniciar sesión con POST /login y reutilizar cookies (-c para guardar, -b para enviar).
- CannotConnectException/Access denied a MySQL:
  - Verifica URL, usuario y contraseña de MySQL; que la BD webestudiantil exista; que el puerto 3306 esté disponible.
- Cambios de esquema:
  - spring.jpa.hibernate.ddl-auto=update permite modificar tablas automáticamente; en producción considera flyway/liquibase.
- Contraseñas:
  - Se guardan cifradas (BCrypt) al crear/actualizar.
- Seguridad de /admin/students:
  - Actualmente es público en SecurityConfig. Cambia a autenticado si no quieres registro abierto.
- JSON en /teacher sin @ResponseBody:
  - El método GET /teacher retorna List<Professor> en un @Controller. Si no recibes JSON, anota el método con @ResponseBody o usa @RestController para el controlador.
- Roles no aplicados en antMatchers:
  - Aunque se asignan roles STUDENT/TEACHER en UserDetails, no hay reglas por rol en SecurityConfig. Si necesitas reglas por rol, añádelas en authorizeHttpRequests.

Tests
- Ejecutar pruebas (si existen): mvn test

Licencia
- (Agregar si aplica)