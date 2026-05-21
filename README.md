# Banreservas Backend Test — API de Gestión de Clientes

Servicio RESTful para gestión de clientes construido con **Quarkus 3.x**, **Java 21** y **PostgreSQL**. Implementa CRUD completo con autenticación JWT, arquitectura hexagonal y validación de datos en profundidad.

---

## Tabla de Contenidos

1. [Descripción de la Solución](#1-descripción-de-la-solución)
2. [Decisiones de Diseño](#11-decisiones-de-diseño)
3. [Arquitectura](#2-arquitectura)
4. [Modelo de Datos](#3-modelo-de-datos)
5. [Requisitos](#4-requisitos)
6. [Configuración e Instalación](#5-configuración-e-instalación)

---

## 1. Descripción de la Solución

Este servicio provee una API RESTful para gestionar la lista de clientes de una empresa. Permite crear, consultar, actualizar y eliminar registros de clientes. El gentilicio del país se resuelve automáticamente a través de la API externa [restcountries.com](https://restcountries.com) usando el código de país ISO 3166 alpha-2 proporcionado al registrar un cliente.

## 2. Decisiones de Diseño
La decision de utilizar esta arquitectura viene dada por la necesidad de aislar la capa de negocio, es decir aislar el dominio de y que el mismo no depende de alguna tecnologia. El negocio no debería saber si los datos vienen de una base de datos, de un API o de un archivo. La forma en como se obtienen los datos es un detalle técnico que puede cambiar. La arquitectura hexagonal fue elegida precisamente para garantizar ese aislamiento: el núcleo de negocio define lo que necesita, y la tecnología se adapta a él — nunca al revés. Esto Permite que si la tecnologia cambia la capa de dominio se mantiene igual sin cambiar y las nuevas tecnologia se adaptan al dominio, esto incluso hace que los cambios sean mas manejables en el tiempo porque todos los detalles tecnicos estan creados en base a interfaces.

## 3. Arquitectura

### 3.1 Arquitectura Hexagonal — Visión General de Capas

El proyecto sigue la **Arquitectura Hexagonal (Puertos y Adaptadores)** la presentación e infraestructura dependen del dominio; el dominio no depende de nada.

**Responsabilidades por capa:**

| Capa | Paquete | Depende de |
|---|---|---|
| Presentación | `presentation/` | Solo la capa de aplicación |
| Aplicación | `application/` | Solo los puertos de dominio |
| Dominio | `domain/` | Nada |
| Infraestructura | `infrastructure/` | Puertos de dominio + Quarkus/JPA |

## 4 Modelo de Datos

| Campo | Requerido | Formato | Notas |
|---|---|---|---|
| `firstName` | Sí | String, máx. 100 chars | Se recorta al guardar |
| `middleName` | No | String, máx. 100 chars | Se recorta al guardar |
| `lastName` | Sí | String, máx. 100 chars | Se recorta al guardar |
| `secondLastName` | No | String, máx. 100 chars | Se recorta al guardar |
| `email` | Sí | Formato de correo válido | Minúsculas, único |
| `address` | Sí | String, máx. 500 chars | Se recorta al guardar |
| `phone` | Sí | String, máx. 30 chars | Se recorta al guardar |
| `country` | Sí | 2 letras ISO 3166 alpha-2 | Mayúsculas; activa la consulta del gentilicio |
| `demonym` | Automático | String | Obtenido de restcountries.com, no lo provee el usuario |

---

## 5. Requisitos

| Herramienta | Versión mínima | Necesaria para |
|---|---|---|
| Java | 21 | Compilar y ejecutar localmente |
| Maven | — | El wrapper `mvnw` / `mvnw.cmd` está incluido, no requiere instalación |
| Docker | 20+ | Dev Services (modo dev/test) y despliegue con Docker Compose |

> Acceso a Internet requerido para llamadas a [restcountries.com](https://restcountries.com) al crear o actualizar clientes.
>
> 
## 6. Configuración e Instalación

### 6.1 Opción A — Docker Compose (recomendado)

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

### 6.2 Opción B — Modo desarrollo local (Maven + Dev Services)

Requiere Java 21 y Docker en ejecución. Quarkus Dev Services aprovisiona PostgreSQL automáticamente.

```bash
./mvnw quarkus:dev            # Linux/macOS
mvnw.cmd quarkus:dev          # Windows
```

La API queda disponible en `http://localhost:8080`.

---



