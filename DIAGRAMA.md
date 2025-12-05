# Diagrama de Arquitectura - API RESTful de Usuarios

## Arquitectura General

```
┌─────────────────────────────────────────────────────────────────┐
│                         Cliente HTTP                            │
│                    (Postman, cURL, Browser)                     │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ HTTP Request/Response
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    Spring Security Layer                        │
│  - Filtros de seguridad                                        │
│  - Endpoint público: /api/users                                 │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                      Controller Layer                           │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  UserController                                          │  │
│  │  - POST /api/users (crear)                               │  │
│  │  - GET /api/users/{id} (obtener)                         │  │
│  │  - GET /api/users (listar todos)                         │  │
│  │  - PUT /api/users/{id} (actualizar)                      │  │
│  │  - PUT /api/users/{id}/password (cambiar password)       │  │
│  │  - DELETE /api/users/{id} (eliminar)                     │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ UserRequestDTO
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                       Service Layer                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  IUserService (Interface)                                │  │
│  │  └── UserService (Implementation)                        │  │
│  │      - Orquesta validaciones                             │  │
│  │      - Crea usuario mediante Factory                     │  │
│  │      - Persiste en BD                                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  IJWTService (Interface)                                  │  │
│  │  └── JWTService (Implementation)                         │  │
│  │      - Genera tokens JWT                                 │  │
│  │      - Valida tokens                                      │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    Validation Layer                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  UserRequestValidator                                    │  │
│  │  - Orquesta todas las validaciones                      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │EmailValidator│  │PasswordValida│  │EmailDuplicati│         │
│  │              │  │tor           │  │onValidator   │         │
│  │- Valida regex│  │- Valida regex│  │- Valida BD   │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
│  ┌──────────────────────┐  ┌──────────────────────┐           │
│  │UserUpdateRequestVali │  │ChangePasswordRequest │           │
│  │dator                 │  │Validator             │           │
│  │- Valida actualización│  │- Valida cambio pass  │           │
│  └──────────────────────┘  └──────────────────────┘           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                      Factory Layer                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  UserFactory                                             │  │
│  │  - Crea entidad User desde DTO                           │  │
│  │  - Encripta password (BCrypt)                            │  │
│  │  - Genera UUID, fechas, token                            │  │
│  │  - Crea entidades Phone asociadas                         │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ User Entity
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                      Mapper Layer                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  IUserMapper (Interface)                                 │  │
│  │  └── UserMapper (Implementation)                        │  │
│  │      - Convierte Entity a DTO                            │  │
│  │      - Convierte DTO a Entity                             │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    Repository Layer                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  UserRepository (JpaRepository)                         │  │
│  │  - findByEmail()                                         │  │
│  │  - save()                                                │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ JPA/Hibernate
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    Database Layer                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  H2 Database (In-Memory)                                 │  │
│  │  - Table: users                                          │  │
│  │  - Table: phones                                         │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Flujos Principales

### Flujo de Registro de Usuario (POST /api/users)

```
1. Cliente → POST /api/users
   │
   ├─► Spring Security (permite acceso público)
   │
   ├─► UserController.createUser()
   │   └─► Valida @Valid en UserRequestDTO
   │
   ├─► UserService.createUser()
   │   │
   │   ├─► UserRequestValidator.validate()
   │   │   ├─► EmailValidator.validate()
   │   │   ├─► PasswordValidator.validate()
   │   │   └─► EmailDuplicationValidator.validate()
   │   │
   │   ├─► JWTService.generateToken()
   │   ├─► UserFactory.createUser()
   │   └─► UserRepository.save()
   │
   ├─► UserMapper.toResponseDTO()
   └─► Retorna UserResponseDTO (201 Created)
```

### Flujo de Actualización (PUT /api/users/{id})

```
1. Cliente → PUT /api/users/{id}
   │
   ├─► UserController.updateUser()
   │
   ├─► UserService.updateUser()
   │   ├─► UserRepository.findById() → UserNotFoundException si no existe
   │   ├─► UserUpdateRequestValidator.validate()
   │   ├─► EmailDuplicationValidator.validate() (si email cambió)
   │   ├─► Actualiza campos del User
   │   └─► UserRepository.save()
   │
   └─► Retorna UserUpdateResponseDTO (200 OK)
```

### Flujo de Cambio de Contraseña (PUT /api/users/{id}/password)

```
1. Cliente → PUT /api/users/{id}/password
   │
   ├─► UserController.changePassword()
   │
   ├─► UserService.changePassword()
   │   ├─► UserRepository.findById() → UserNotFoundException si no existe
   │   ├─► ChangePasswordRequestValidator.validate()
   │   │   ├─► PasswordValidator.validate()
   │   │   └─► Valida que password == confirmPassword
   │   ├─► Encripta nueva password (BCrypt)
   │   └─► UserRepository.save()
   │
   └─► Retorna ChangePasswordResponseDTO (200 OK)
```

### Flujo de Eliminación (DELETE /api/users/{id})

```
1. Cliente → DELETE /api/users/{id}
   │
   ├─► UserController.deleteUser()
   │
   ├─► UserService.deleteUser()
   │   ├─► UserRepository.findById() → UserNotFoundException si no existe
   │   └─► UserRepository.delete()
   │
   └─► Retorna 204 No Content
```

## Manejo de Excepciones

```
┌─────────────────────────────────────────────────────────────┐
│              GlobalExceptionHandler                         │
│  (@ControllerAdvice)                                        │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  EmailAlreadyExistsException                          │  │
│  │  └─► 409 Conflict                                     │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  InvalidEmailFormatException                          │  │
│  │  └─► 400 Bad Request                                   │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  InvalidPasswordFormatException                       │  │
│  │  └─► 400 Bad Request                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  PasswordMismatchException                            │  │
│  │  └─► 400 Bad Request                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UserNotFoundException                                │  │
│  │  └─► 404 Not Found                                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  MethodArgumentNotValidException                      │  │
│  │  └─► 400 Bad Request                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Exception (genérico)                                 │  │
│  │  └─► 500 Internal Server Error                        │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  Todas retornan: {"mensaje": "..."}                        │
└─────────────────────────────────────────────────────────────┘
```

## Modelo de Datos

```
┌─────────────────────┐
│       User          │
├─────────────────────┤
│ id: UUID (PK)       │
│ name: String        │
│ email: String (UK)  │
│ password: String    │
│ created: DateTime   │
│ modified: DateTime  │
│ lastLogin: DateTime │
│ token: String       │
│ isActive: Boolean   │
└──────────┬──────────┘
           │
           │ 1:N
           │
┌──────────▼──────────┐
│       Phone         │
├─────────────────────┤
│ id: UUID (PK)       │
│ number: String      │
│ citycode: String    │
│ countrycode: String │
│ user_id: UUID (FK)  │
└─────────────────────┘
```

## Componentes y Responsabilidades

### Controller
- **UserController**: Recibe peticiones HTTP, valida entrada, delega a servicio

### Service
- **IUserService/UserService**: Orquesta la creación de usuarios
- **IJWTService/JWTService**: Maneja generación y validación de tokens JWT

### Validator
- **UserRequestValidator**: Coordina validaciones para creación
- **UserUpdateRequestValidator**: Coordina validaciones para actualización
- **ChangePasswordRequestValidator**: Valida cambio de contraseña
- **EmailValidator**: Valida formato de email
- **PasswordValidator**: Valida formato de password
- **EmailDuplicationValidator**: Valida que email no exista

### Factory
- **UserFactory**: Crea entidades User completas desde DTOs

### Mapper
- **IUserMapper/UserMapper**: Convierte entre Entity y DTO

### Repository
- **UserRepository**: Acceso a datos mediante JPA

### Exception
- **GlobalExceptionHandler**: Manejo centralizado de excepciones
- Excepciones de dominio: 
  - EmailAlreadyExistsException
  - InvalidEmailFormatException
  - InvalidPasswordFormatException
  - PasswordMismatchException
  - UserNotFoundException

### Config
- **SecurityConfig**: Configuración de Spring Security
- **JWTConfig**: Configuración de JWT
- **SwaggerConfig**: Configuración de documentación API

## Principios Aplicados

- **Single Responsibility**: Cada clase tiene una única responsabilidad
- **Dependency Inversion**: Dependencias sobre interfaces
- **Open/Closed**: Extensible mediante interfaces sin modificar código
- **Strategy Pattern**: Validadores intercambiables
- **Factory Pattern**: Creación de objetos complejos
- **Repository Pattern**: Abstracción del acceso a datos
- **DTO Pattern**: Separación entre capas

