# Manufacturing Overall Equipment Effectiveness (OEE) Analytics System

[![Java Version](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-MySQL%208.x%20(InnoDB)-blue.svg)](https://www.mysql.com/)
[![Architecture](https://img.shields.io/badge/Architecture-Modular%20Monolith-purple.svg)]()
[![Status](https://img.shields.io/badge/Status-Phase%203%20Completed-success.svg)]()

An enterprise-grade manufacturing telemetry analytics platform that models factory operations, tracks high-frequency machine states, processes production counter pulses and quality inspections, calculates deterministic Overall Equipment Effectiveness (OEE) metrics, detects synthetic telemetry anomalies, and exports reproducible benchmarks.

---

## 1. Project Overview

The system is designed to simulate and analyze a realistic industrial discrete manufacturing plant operating across three 8-hour shifts per day over a 90-day production history.

### Core Scale & Telemetry Scope:
* **Plant:** 1 discrete manufacturing facility.
* **Production Lines:** 5 specialized production lines.
* **Machines:** 30 workstations (mix of bottleneck and secondary assets).
* **Products:** 20 distinct SKU profiles with effective-dated Ideal Cycle Times (ICT).
* **Temporal Scope:** 90 consecutive calendar days across three 8-hour shifts (including overnight 22:00–06:00 shifts).
* **Machine State Events:** ~100,000 discrete operational events (`RUNNING`, `UNPLANNED_DOWNTIME`, `PLANNED_DOWNTIME`, `CHANGEOVER`).
* **Production Counts & Quality Inspections:** ~500,000 combined granular telemetry records.
* **Fixed Random Seed:** `20261022` for 100% reproducible synthetic dataset generation.

---

## 2. Technology Stack

| Layer | Technology | Specification / Justification |
|---|---|---|
| **Language Runtime** | Java 21 LTS | Records, Pattern Matching, Virtual Threads readiness, enhanced performance |
| **Framework** | Spring Boot 3.3.5 | Modern dependency injection, embedded Tomcat, production actuator |
| **Build System** | Maven & Maven Wrapper | Deterministic multi-platform builds without local Maven installation |
| **Database Engine** | MySQL 8.0+ | InnoDB storage engine, ACID transactions, `utf8mb4` character set |
| **ORM / Persistence** | Spring Data JPA / Hibernate 6.x | Normalized 3NF entity persistence, type-safe queries, HikariCP pooling |
| **Database Migrations** | Flyway 10.x (`flyway-mysql`) | Versioned schema migrations, baseline reproducibility (Phase 4) |
| **API Documentation** | SpringDoc OpenAPI 2.6.0 | Interactive Swagger UI (`/swagger-ui.html`) and OpenAPI 3 schema |
| **Validation** | Jakarta Bean Validation | Declarative API payload validation (`@Valid`, `@NotNull`, `@Size`) |
| **Logging** | SLF4J with Logback | Colorized console formatting, 30-day rolling file appenders, error separation |

---

## 3. Project Architecture

The application is structured as a **Modular Monolith** organized by Domain-Driven Design (DDD) bounded contexts:

```text
src/main/java/com/manufacturing/oee/
│
├── ManufacturingOeeApplication.java     # Application Bootstrap Entrypoint
│
├── config/                             # Infrastructure Configurations
│   ├── OpenApiConfig.java              # Swagger 3 & OpenAPI Metadata
│   └── ApplicationConfig.java          # Testable UTC Clock & Core Beans
│
├── common/                             # Shared Kernel & Cross-Cutting Concerns
│   ├── constants/AppConstants.java     # Routing constants, temporal formats, seeds
│   ├── response/                       # Standardized ApiResponse, ErrorResponse, PageResponse
│   ├── exception/                      # GlobalExceptionHandler, BusinessException, NotFound
│   └── util/DateTimeUtil.java          # Deterministic UTC ISO-8601 formatting helpers
│
├── system/                             # System Foundation Diagnostics
│   ├── SystemStatusController.java     # Smoke check endpoint (/api/v1/system/status)
│   └── dto/SystemStatusResponse.java   # Status payload DTO
│
├── masterdata/                         # Plant, Line, Machine, Product, Shift, Downtime
├── production/                         # Work Orders, Pulse Counters, Corrections
├── machinestate/                       # State Events, Micro-Stops, Loss Allocations
├── quality/                            # Inspection Batches, Scrap vs Rework
├── oee/                                # OEE Engines, Aggregations, Run Auditing
├── anomaly/                            # Statistical Detectors, Ground Truth Benchmarks
├── audit/                              # Immutable Event Ledger, JSON Snapshots
└── export/                             # Reproducible Dataset Exporters (SHA-256)
```

---

## 4. Getting Started

### Prerequisites
* **Java 21 LTS** installed (`java -version` should report Java 21).
* **MySQL 8.x** installed locally and running on port `3306`.
* **Git** installed.

### 1. Database Creation
Connect to your local MySQL 8.x instance and create the target schema:

```sql
CREATE DATABASE IF NOT EXISTS manufacturing_oee
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
```

### 2. Environment Configuration
Copy the configuration template:

```bash
cp .env.example .env
```

Ensure `DB_USERNAME` and `DB_PASSWORD` in `.env` (or in your environment) match your local MySQL credentials.

### 3. Build & Compile Check
Using the Maven Wrapper included in the repository:

**On Windows (PowerShell / Command Prompt):**
```powershell
.\mvnw.cmd clean compile
```

**On Linux / macOS:**
```bash
chmod +x mvnw
./mvnw clean compile
```

### 4. Running the Application Locally
Launch the application with the `dev` profile:

**On Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

**On Linux / macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on port `8080`.

---

## 5. API Documentation & Endpoints

Once the application is running, the following endpoints are available:

* **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI 3 JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
* **System Foundation Status:** `GET` [http://localhost:8080/api/v1/system/status](http://localhost:8080/api/v1/system/status)
* **Actuator Health:** `GET` [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Standard Success Envelope
```json
{
  "success": true,
  "message": "System foundation operational",
  "timestamp": "2026-11-13T14:30:00.000Z",
  "data": {
    "application": "manufacturing-oee-analytics",
    "status": "UP",
    "version": "0.0.1-SNAPSHOT",
    "timestamp": "2026-11-13T14:30:00.000Z",
    "activeProfiles": ["dev"],
    "message": "Manufacturing OEE Analytics System - Phase 3 Foundation Ready"
  }
}
```

### Standard Error Envelope
```json
{
  "timestamp": "2026-11-13T14:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Machine with identifier 'MCH-999' was not found",
  "path": "/api/v1/machines/MCH-999"
}
```

---

## 6. Project Documentation Index

Detailed engineering specifications are maintained in the repository:

### Phase 1 — Requirements & Business Rules Baseline
1. [requirements.md](requirements.md) — Fixed scale, 11 core entities, and technical boundaries.
2. [functional-requirements.md](functional-requirements.md) — FR-01 through FR-40 functional catalog.
3. [business-rules.md](business-rules.md) — BR-01 through BR-45 operational domain constraints.
4. [oee-calculation-policy.md](oee-calculation-policy.md) — Mathematical formulations for Availability, Performance, Quality, and OEE.
5. [anomaly-detection-rules.md](anomaly-detection-rules.md) — 7 synthetic anomaly injection profiles and detection logic.
6. [validation-rules.md](validation-rules.md) — Relational and domain validation rules.
7. [assumptions-and-open-questions.md](assumptions-and-open-questions.md) — 12 pending business decisions register.
8. [non-functional-requirements.md](non-functional-requirements.md) — NFR-01 through NFR-28 quality attributes.
9. [phase-1-completion-report.md](phase-1-completion-report.md) — Phase 1 sign-off report.

### Phase 2 — Database Design & Data Modelling
10. [database-design.md](database-design.md) — 3NF relational normalization and architectural design.
11. [erd.md](erd.md) — Mermaid ERD for all 19 entities and cardinality matrix.
12. [table-specifications.md](table-specifications.md) — DDL specifications, columns, data types, and constraints.
13. [data-dictionary.md](data-dictionary.md) — Comprehensive attribute definitions, enums, and units.
14. [database-constraints-and-indexes.md](database-constraints-and-indexes.md) — Referential integrity and B-Tree indexes.
15. [database-design-decisions.md](database-design-decisions.md) — Architectural decision record (ADR).
16. [phase-2-completion-report.md](phase-2-completion-report.md) — Phase 2 sign-off report.

### Phase 3 — Technical Foundation & Spring Boot Setup
17. [project-setup.md](project-setup.md) — Comprehensive local workstation setup guide.
18. [project-architecture.md](project-architecture.md) — Monolith architecture, packaging, and conventions.
19. [dependency-documentation.md](dependency-documentation.md) — Dependency justification and compatibility matrix.
20. [phase-3-completion-report.md](phase-3-completion-report.md) — Phase 3 deliverables and verification audit.

---


