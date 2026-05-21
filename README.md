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

## 2. Arquitectura

### 2.1 Arquitectura Hexagonal — Visión General de Capas

El proyecto sigue la **Arquitectura Hexagonal (Puertos y Adaptadores)** la presentación e infraestructura dependen del dominio; el dominio no depende de nada.

**Responsabilidades por capa:**

| Capa | Paquete | Depende de |
|---|---|---|
| Presentación | `presentation/` | Solo la capa de aplicación |
| Aplicación | `application/` | Solo los puertos de dominio |
| Dominio | `domain/` | Nada |
| Infraestructura | `infrastructure/` | Puertos de dominio + Quarkus/JPA |


