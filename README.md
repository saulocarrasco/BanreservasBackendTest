# Banreservas Backend Test — API de Gestión de Clientes

Servicio RESTful para gestión de clientes construido con **Quarkus 3.x**, **Java 21** y **PostgreSQL**. Implementa CRUD completo con autenticación JWT, arquitectura hexagonal y validación de datos en profundidad.

---

## Tabla de Contenidos

1. [Descripción de la Solución](#1-descripción-de-la-solución)
2. [Arquitectura](#2-arquitectura)
3. [Modelo de Datos](#3-modelo-de-datos)
4. [Requisitos](#4-requisitos)
5. [Configuración e Instalación](#5-configuración-e-instalación)
6. [Autenticación](#6-autenticación)
7. [Referencia de API](#7-referencia-de-api)
8. [Manejo de Errores](#8-manejo-de-errores)
9. [Validación de Datos](#9-validación-de-datos)
10. [Pruebas](#10-pruebas)
11. [Decisiones de Diseño](#11-decisiones-de-diseño)

---

## 1. Descripción de la Solución

Este servicio provee una API RESTful para gestionar la lista de clientes de una empresa. Permite crear, consultar, actualizar y eliminar registros de clientes. El gentilicio del país se resuelve automáticamente a través de la API externa [restcountries.com](https://restcountries.com) usando el código de país ISO 3166 alpha-2 proporcionado al registrar un cliente.

**Capacidades principales:**
- Operaciones CRUD completas sobre clientes
- Filtrar clientes por país
- Listado paginado de clientes
- Autenticación basada en JWT (RS256)
- Validación de entrada en capas DTO, servicio y base de datos
- Respuestas de error en formato RFC 7807 Problem Details
- Bloqueo optimista para actualizaciones concurrentes

---

## 2. Arquitectura

El proyecto sigue la **Arquitectura Hexagonal (Puertos y Adaptadores)**. Las reglas de negocio son completamente independientes de los detalles técnicos de cómo se implementan — el dominio no sabe nada de bases de datos, frameworks ni protocolos. La infraestructura se adapta al negocio, nunca al revés.

**Responsabilidades por capa:**

| Capa | Paquete | Depende de |
|---|---|---|
| Presentación | `presentation/` | Solo la capa de aplicación |
| Aplicación | `application/` | Solo los puertos de dominio |
| Dominio | `domain/` | Nada |
| Infraestructura | `infrastructure/` | Puertos de dominio + Quarkus/JPA |

---

### 2.1 Diagrama De Arquitectura

<img width="1489" height="900" alt="image" src="https://github.com/user-attachments/assets/be35c63c-6f88-45b0-a5c1-7e77f3e0f2b5" />


## 3. Modelo de Datos

### Campos del Cliente

| Campo | Requerido | Formato | Notas |
|---|---|---|---|
| `firstName` | Sí | String, máx. 100 chars | |
| `middleName` | No | String, máx. 100 chars | |
| `lastName` | Sí | String, máx. 100 chars | |
| `secondLastName` | No | String, máx. 100 chars | |
| `email` | Sí | Formato de correo válido | Minúsculas, único |
| `address` | Sí | String, máx. 500 chars | |
| `phone` | Sí | String, máx. 30 chars | |
| `country` | Sí | 2 letras ISO 3166 alpha-2 | Mayúsculas; activa la consulta del gentilicio |
| `demonym` | Automático | String | Obtenido de restcountries.com, no lo provee el usuario |

---

## 4. Requisitos

| Herramienta | Versión mínima | Necesaria para |
|---|---|---|
| Java | 21 | Compilar y ejecutar localmente |
| Maven | — | El wrapper `mvnw` / `mvnw.cmd` está incluido, no requiere instalación |
| Docker | 20+ | Dev Services (modo dev/test) y despliegue con Docker Compose |

> Acceso a Internet requerido para llamadas a [restcountries.com](https://restcountries.com) al crear o actualizar clientes.

---

## 5. Configuración e Instalación

### 5.1 Opción A — Docker Compose (recomendado)

Levanta la aplicación y PostgreSQL con un solo comando. No requiere Java instalado localmente.

```bash
# 1. Compilar el JAR
./mvnw package -DskipTests     # Linux/macOS
mvnw.cmd package -DskipTests   # Windows

# 2. Construir la imagen y levantar todos los servicios
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

Para detener:

```bash
docker compose down
```

**Variables de entorno configuradas por Docker Compose:**

| Variable | Valor |
|---|---|
| `QUARKUS_DATASOURCE_JDBC_URL` | `jdbc:postgresql://postgres:5432/customerdb` |
| `QUARKUS_DATASOURCE_USERNAME` | `banreservas` |
| `QUARKUS_DATASOURCE_PASSWORD` | `banreservas123` |
| `QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION` | `update` |
| `QUARKUS_REST_CLIENT_COUNTRY_API_URL` | `https://restcountries.com` |

---

### 5.2 Opción B — Modo desarrollo local (Maven + Dev Services)

Requiere Java 21 y Docker en ejecución. Quarkus Dev Services aprovisiona PostgreSQL automáticamente.

```bash
./mvnw quarkus:dev            # Linux/macOS
mvnw.cmd quarkus:dev          # Windows
```

La API queda disponible en `http://localhost:8080`.

---

### 5.3 Ejecutar pruebas

```bash
./mvnw test
```

> Requiere Docker en ejecución — Dev Services aprovisiona PostgreSQL automáticamente para las pruebas de integración.

---

## 6. Autenticación

Todos los endpoints `/customers` requieren un Bearer JWT. Los tokens se firman con RS256 (RSA-2048).

### Obtener un token

```http
POST /auth/token
Content-Type: application/json

{
  "username": "admin",
  "password": "secret123"
}
```

**Respuesta `200 OK`:**
```json
{ "token": "eyJhbGciOiJSUzI1NiJ9..." }
```

**Usar el token en solicitudes posteriores:**
```http
Authorization: Bearer <token>
```

> Credenciales de demo: `admin` / `secret123`. Reemplazar con variables de entorno o un proveedor de identidad en producción.

---

## 7. Referencia de API

### Resumen de Endpoints

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/auth/token` | Público | Obtener token JWT |
| `POST` | `/customers` | Requerida | Crear un cliente |
| `GET` | `/customers` | Requerida | Listar todos los clientes |
| `GET` | `/customers?country=DO` | Requerida | Filtrar por país (ISO 3166 alpha-2) |
| `GET` | `/customers?page=0&size=10` | Requerida | Listado paginado |
| `GET` | `/customers/{id}` | Requerida | Obtener cliente por ID |
| `PATCH` | `/customers/{id}` | Requerida | Actualización parcial (email, dirección, teléfono, país) |
| `DELETE` | `/customers/{id}` | Requerida | Eliminar cliente |

---

### POST /customers — Crear Cliente

**Cuerpo de la solicitud:**
```json
{
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "Pérez",
  "secondLastName": "García",
  "email": "juan@example.com",
  "address": "Calle 1, Santo Domingo",
  "phone": "+1-809-555-0100",
  "country": "DO"
}
```

- `middleName` y `secondLastName` son opcionales.
- `country` debe ser un código ISO 3166 alpha-2 válido de 2 letras.
- El `demonym` se obtiene de restcountries.com y se almacena automáticamente.

**Respuesta `201 Created`:**
```json
{
  "id": 1,
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "Pérez",
  "secondLastName": "García",
  "email": "juan@example.com",
  "address": "Calle 1, Santo Domingo",
  "phone": "+1-809-555-0100",
  "country": "DO",
  "demonym": "Dominican",
  "createdAt": "2026-05-19T10:00:00",
  "updatedAt": "2026-05-19T10:00:00"
}
```

---

### GET /customers — Listar Todos los Clientes

```http
GET /customers
Authorization: Bearer <token>
```

**Respuesta `200 OK`:**
```json
[
  { "id": 1, "firstName": "Juan", ... },
  { "id": 2, "firstName": "María", ... }
]
```

---

### GET /customers?country=DO — Filtrar por País

```http
GET /customers?country=DO
Authorization: Bearer <token>
```

Retorna solo los clientes con `country = "DO"`.

---

### GET /customers?page=0&size=10 — Listado Paginado

```http
GET /customers?page=0&size=10
Authorization: Bearer <token>
```

**Respuesta `200 OK`:**
```json
{
  "data": [ { "id": 1, ... }, { "id": 2, ... } ],
  "page": 0,
  "size": 10,
  "total": 25,
  "totalPages": 3
}
```

---

### GET /customers/{id} — Obtener por ID

```http
GET /customers/1
Authorization: Bearer <token>
```

**Respuesta `200 OK`:** objeto `CustomerResponse` individual.  
**Respuesta `404 Not Found`:** error en formato Problem+JSON.

---

### PATCH /customers/{id} — Actualización Parcial

Solo se pueden modificar `email`, `address`, `phone` y `country`. Enviar únicamente los campos a cambiar.

```http
PATCH /customers/1
Authorization: Bearer <token>
Content-Type: application/json

{ "country": "US" }
```

Si `country` cambia, el gentilicio se obtiene nuevamente de forma automática.

**Respuesta `200 OK`:** `CustomerResponse` actualizado.

---

### DELETE /customers/{id} — Eliminar Cliente

```http
DELETE /customers/1
Authorization: Bearer <token>
```

**Respuesta `204 No Content`** en caso de éxito.  
**Respuesta `404 Not Found`** si el cliente no existe.

---

## 8. Manejo de Errores

Todos los errores siguen el estándar [RFC 7807 Problem Details](https://datatracker.ietf.org/doc/html/rfc7807):

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Customer 999 not found"
}
```

| Escenario | Estado HTTP | Título |
|---|---|---|
| JWT ausente o inválido | 401 | Unauthorized |
| Error de validación (campo faltante, formato incorrecto) | 400 | Validation Error |
| Email duplicado | 409 | Conflict |
| Cliente no encontrado | 404 | Not Found |
| Código de país inválido | 422 | Unprocessable Entity |
| Servicio de países no disponible | 503 | Service Unavailable |
| Conflicto de PATCH concurrente | 409 | Conflict |
| Error inesperado del servidor | 500 | Internal Server Error |

---

## 9. Validación de Datos

Validación en profundidad: se aplica en cada capa para que ningún punto de fallo único pueda corromper la base de datos.

| Capa | Medida |
|---|---|
| **DTO** | `@NotBlank`, `@Email`, `@Size(min=2,max=2)`, `@Pattern([A-Za-z]{2})` — falla rápido antes de la lógica de negocio |
| **Servicio — normalización** | Email recortado y en minúsculas; país recortado y en mayúsculas. Previene duplicados por variación de mayúsculas. |
| **Servicio — validaciones semánticas** | `existsByEmail` antes de insertar/actualizar → 409. Consulta de país vía restcountries.com → 422 si es desconocido. |
| **`@Transactional`** | Todas las escrituras en una sola transacción; un fallo en la consulta de país revierte toda la operación. |
| **`@Version` (bloqueo optimista)** | PATCH concurrente sobre el mismo registro: el primero gana, el segundo recibe 409. Sin pérdida de actualizaciones. |
| **Restricciones de base de datos** | `UNIQUE` en email y `NOT NULL` en columnas requeridas como última línea de defensa. |

---

## 10. Pruebas

### Descripción del conjunto de pruebas

| Clase | Tipo | Cobertura |
|---|---|---|
| `CustomerServiceTest` | Unitaria (Mockito, sin contenedor) | 13 pruebas — todas las ramas del servicio |
| `CustomerResourceTest` | Integración (`@QuarkusTest` + BD real) | 12 pruebas — capa HTTP y cableado |
| `CustomerValidationTest` | Integración | 7 pruebas — reglas de validación de entrada |
| `AuthResourceTest` | Integración | 5 pruebas — endpoint de autenticación y JWT |

### Ejecutar pruebas

```bash
./mvnw test
```

> Las pruebas de integración requieren Docker en ejecución (Dev Services aprovisiona PostgreSQL automáticamente).

### Estrategia de pruebas

- **Pruebas unitarias** usan mocks de Mockito para `CustomerRepository` y `CountryGateway` — sin contenedor, sin base de datos, se ejecutan en menos de 1 segundo.
- **Pruebas de integración** usan `@QuarkusTest` con PostgreSQL real (Dev Services), `@InjectMock CountryGateway` (seguro sin conexión, determinista) y `@TestSecurity` para omitir la verificación JWT de forma que las pruebas se enfoquen en el comportamiento de negocio.
- Cada prueba de integración comienza con una base de datos limpia (`@BeforeEach` trunca la tabla de clientes).

---

## 11. Decisiones de Diseño

Decisiones clave de un vistazo:

| Decisión | Elección | Justificación |
|---|---|---|
| Arquitectura | Hexagonal (Puertos y Adaptadores) | Las reglas de negocio son independientes de los detalles técnicos de implementación |
| Autenticación | JWT RS256 (SmallRye JWT) | Sin estado, no requiere almacén de sesiones |
| Formato de errores | RFC 7807 Problem+JSON | Estándar de la industria; legible por máquinas |
| Almacenamiento del gentilicio | Guardado en el registro del cliente | Evita llamadas HTTP en cada GET; se actualiza al cambiar el país |
| Generación del esquema | `update` (dev), `drop-and-create` (test) | Dev preserva los datos; test siempre empieza limpio |
| Verificación de credenciales | `MessageDigest.isEqual` | Comparación en tiempo constante para prevenir ataques de temporización |
| Escrituras concurrentes | Bloqueo optimista `@Version` | Sin overhead de bloqueo explícito; seguro para cargas de baja contención |
