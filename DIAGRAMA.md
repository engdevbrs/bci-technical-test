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
│  │  - POST /api/users                                       │  │
│  │  - Validación de entrada (@Valid)                        │  │
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

## Flujo de Registro de Usuario

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
   │   │   │
   │   │   ├─► EmailValidator.validate()
   │   │   │   └─► Valida regex de email
   │   │   │
   │   │   ├─► PasswordValidator.validate()
   │   │   │   └─► Valida regex de password
   │   │   │
   │   │   └─► EmailDuplicationValidator.validate()
   │   │       └─► UserRepository.findByEmail()
   │   │
   │   ├─► JWTService.generateToken()
   │   │   └─► Genera token JWT
   │   │
   │   ├─► UserFactory.createUser()
   │   │   ├─► UserMapper.toEntity()
   │   │   ├─► Encripta password (BCrypt)
   │   │   ├─► Genera UUID
   │   │   ├─► Establece fechas (created, modified, lastLogin)
   │   │   └─► Crea entidades Phone
   │   │
   │   └─► UserRepository.save()
   │       └─► Persiste User y Phones
   │
   ├─► UserMapper.toResponseDTO()
   │   └─► Convierte Entity a DTO
   │
   └─► Retorna UserResponseDTO (201 Created)
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
- **UserRequestValidator**: Coordina todas las validaciones
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
- Excepciones de dominio: EmailAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException

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

