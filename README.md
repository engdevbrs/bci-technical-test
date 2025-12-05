# API RESTful de Usuarios - BCI Technical Test

API RESTful para registro y gestión de usuarios desarrollada con Spring Boot.

## Stack Tecnológico

- Java 8+
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA / Hibernate
- H2 Database (en memoria)
- JWT para tokens
- Swagger para documentación
- Maven
- JUnit 5 para tests

## Requisitos

- JDK 8 o superior
- Maven 3.6+

## Cómo Ejecutar

1. Clonar el repositorio
2. Compilar:
   ```bash
   mvn clean install
   ```
3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
4. La app corre en `http://localhost:8080`

## Endpoints Disponibles

Todos los endpoints trabajan con JSON, tanto para request como response. Los errores también vienen en JSON con el formato `{"mensaje": "..."}`.

### POST /api/users - Crear usuario

Crea un nuevo usuario en el sistema.

**Request:**
```json
{
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.cl",
  "password": "hunter123",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "countrycode": "57"
    }
  ]
}
```

**Response (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "created": "2024-01-15T10:30:00",
  "modified": "2024-01-15T10:30:00",
  "last_login": "2024-01-15T10:30:00",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "isactive": true
}
```

### GET /api/users/{id} - Obtener usuario

Devuelve los datos de un usuario específico.

**Response (200):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.cl",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "countrycode": "57"
    }
  ],
  "is_active": true
}
```

### GET /api/users - Listar usuarios

Obtiene todos los usuarios registrados.

**Response (200):** Array con todos los usuarios.

### PUT /api/users/{id} - Actualizar usuario

Actualiza los datos de un usuario.

**Request:**
```json
{
  "name": "Juan Rodriguez Actualizado",
  "email": "juan.actualizado@rodriguez.cl",
  "phones": [
    {
      "number": "1111111",
      "citycode": "1",
      "countrycode": "57"
    }
  ]
}
```

### PUT /api/users/{id}/password - Cambiar contraseña

Cambia la contraseña de un usuario.

**Request:**
```json
{
  "password": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**Response (200):**
```json
{
  "mensaje": "Contraseña cambiada con éxito"
}
```

### DELETE /api/users/{id} - Eliminar usuario

Elimina un usuario. Retorna 204 sin body.

## Códigos HTTP

- 200: OK
- 201: Usuario creado
- 204: Usuario eliminado
- 400: Error de validación
- 404: Usuario no encontrado
- 409: Email ya registrado
- 500: Error del servidor

## Errores

Todos los errores vienen en este formato:
```json
{
  "mensaje": "El correo ya registrado"
}
```

Algunos ejemplos:
- `"El formato del correo no es válido"` (400)
- `"El formato de la contraseña no es válido"` (400)
- `"El correo ya registrado"` (409)
- `"Usuario no encontrado"` (404)

## Validaciones

### Email
Debe terminar en `.cl`. Ejemplos válidos:
- `juan@rodriguez.cl`
- `maria.garcia@empresa.cl`

No válidos:
- `juan@rodriguez.com` (no termina en .cl)
- `juan@rodriguez` (sin dominio)

### Password
Por defecto requiere mínimo 8 caracteres alfanuméricos. Se puede cambiar en `application.properties`.

Ejemplos válidos: `hunter123`, `password123`
No válidos: `short` (muy corto)

### Campos Requeridos
- `name`: No puede estar vacío
- `email`: Debe ser válido y terminar en .cl
- `password`: Debe cumplir el formato
- `phones`: Array con al menos un teléfono. Cada teléfono necesita:
  - `number`
  - `citycode`
  - `countrycode`

## Configuración

Todo está en `src/main/resources/application.properties`:
- `validation.email.regex`: Regex para validar emails
- `validation.password.regex`: Regex para validar passwords
- `jwt.secret`: Clave secreta para JWT
- `jwt.expiration`: Tiempo de expiración del token (ms)

## Base de Datos

Usa H2 en memoria. Para ver los datos:

1. Abre `http://localhost:8080/h2-console`
2. Conecta con:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Usuario: `sa`
   - Password: (vacío)

Las tablas se crean automáticamente al iniciar. Si necesitas el schema manual, está en `database-schema.sql`.

## Swagger

La documentación interactiva está en:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- API Docs JSON: `http://localhost:8080/v3/api-docs`

Desde Swagger puedes probar todos los endpoints directamente.

## Tests

Para ejecutar los tests:
```bash
mvn test
```

Cubre servicios, controladores y validadores.

## Cobertura de Código

Si quieres ver qué tan bien están cubiertos los tests, el proyecto tiene JaCoCo configurado. Es bastante simple de usar.

Para generar el reporte, ejecuta:

```bash
mvn test jacoco:report
```

O si quieres empezar desde cero:

```bash
mvn clean test jacoco:report
```

Una vez que termine, el reporte HTML queda en `target/site/jacoco/index.html`. Abre ese archivo en el navegador y verás un resumen con la cobertura por paquete, por clase, y hasta puedes ver línea por línea qué está cubierto (verde) y qué no (rojo).

Si ya ejecutaste los tests antes y solo quieres regenerar el reporte, puedes usar:

```bash
mvn jacoco:report
```

## Ejemplos de Uso

### Con Swagger (más fácil)

1. Inicia la app: `mvn spring-boot:run`
2. Abre `http://localhost:8080/swagger-ui/index.html`
3. Expande el endpoint que quieras probar
4. Click en "Try it out"
5. Completa el JSON y ejecuta

### Con cURL

Crear usuario:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Juan Rodriguez\",\"email\":\"juan@rodriguez.cl\",\"password\":\"hunter123\",\"phones\":[{\"number\":\"1234567\",\"citycode\":\"1\",\"countrycode\":\"57\"}]}"
```

Obtener usuario:
```bash
curl -X GET http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json"
```

Actualizar:
```bash
curl -X PUT http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Juan Actualizado\",\"email\":\"juan.actualizado@rodriguez.cl\",\"phones\":[{\"number\":\"1111111\",\"citycode\":\"1\",\"countrycode\":\"57\"}]}"
```

Cambiar password:
```bash
curl -X PUT http://localhost:8080/api/users/{id}/password \
  -H "Content-Type: application/json" \
  -d "{\"password\":\"newpassword123\",\"confirmPassword\":\"newpassword123\"}"
```

Eliminar:
```bash
curl -X DELETE http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json"
```

### Con Postman

1. Método: POST/GET/PUT/DELETE
2. URL: `http://localhost:8080/api/users` (o con `/{id}`)
3. Headers: `Content-Type: application/json`
4. Body (para POST/PUT): JSON con los datos

O importa desde Swagger: ve a `http://localhost:8080/v3/api-docs`, copia el JSON e impórtalo en Postman.

## Arquitectura

La app sigue una arquitectura en capas:
- **Controller**: Maneja las peticiones HTTP
- **Service**: Lógica de negocio
- **Repository**: Acceso a datos
- **Entity**: Modelo de dominio
- **DTO**: Objetos de transferencia
- **Mapper**: Convierte entre entidades y DTOs
- **Validator**: Validaciones de negocio
- **Factory**: Crea objetos complejos

## Notas

- Passwords se encriptan con BCrypt
- Tokens JWT para autenticación
- El endpoint de registro es público (sin auth)
- CSRF deshabilitado para API REST
- Se aplicaron principios SOLID y varios patrones de diseño (Repository, DTO, Factory, etc.)

---

Desarrollado para la prueba técnica de BCI.
