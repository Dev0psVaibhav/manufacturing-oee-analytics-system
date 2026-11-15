-- ==============================================================================
-- Manufacturing OEE Analytics System - Phase 4 Master Data Schema
-- Dialect: MySQL 8.x
-- Storage Engine: InnoDB
-- Character Encoding: utf8mb4
-- Collation: utf8mb4_0900_ai_ci
-- Manual Execution: Run via MySQL Workbench or MySQL CLI
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS manufacturing_oee
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE manufacturing_oee;

-- ------------------------------------------------------------------------------
-- 1. Table: plant
-- Purpose: Top-level manufacturing facility master record.
-- ------------------------------------------------------------------------------
DROP TABLE IF EXISTS machine;
DROP TABLE IF EXISTS production_line;
DROP TABLE IF EXISTS plant;

CREATE TABLE plant (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    plant_code VARCHAR(20) NOT NULL,
    plant_name VARCHAR(100) NOT NULL,
    time_zone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    country_code VARCHAR(3) NOT NULL DEFAULT 'USA',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_plant PRIMARY KEY (id),
    CONSTRAINT uk_plant_code UNIQUE (plant_code),
    CONSTRAINT chk_plant_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 2. Table: production_line
-- Purpose: Discrete production lines/value streams belonging to a plant.
-- ------------------------------------------------------------------------------
CREATE TABLE production_line (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    plant_id BIGINT UNSIGNED NOT NULL,
    line_code VARCHAR(20) NOT NULL,
    line_name VARCHAR(100) NOT NULL,
    sequence_order INT UNSIGNED NOT NULL,
    line_type VARCHAR(30) NOT NULL DEFAULT 'DISCRETE',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_production_line PRIMARY KEY (id),
    CONSTRAINT fk_production_line_plant FOREIGN KEY (plant_id)
        REFERENCES plant (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_production_line_code UNIQUE (plant_id, line_code),
    CONSTRAINT uk_production_line_seq UNIQUE (plant_id, sequence_order),
    INDEX idx_production_line_plant (plant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 3. Table: machine
-- Purpose: Workstation physical equipment assets mapped to production lines.
-- ------------------------------------------------------------------------------
CREATE TABLE machine (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    line_id BIGINT UNSIGNED NOT NULL,
    machine_code VARCHAR(30) NOT NULL,
    machine_name VARCHAR(100) NOT NULL,
    station_sequence INT UNSIGNED NOT NULL,
    asset_tag VARCHAR(50) NULL,
    is_pacing_machine BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_machine PRIMARY KEY (id),
    CONSTRAINT fk_machine_line FOREIGN KEY (line_id)
        REFERENCES production_line (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_machine_code UNIQUE (machine_code),
    CONSTRAINT uk_machine_line_seq UNIQUE (line_id, station_sequence),
    CONSTRAINT chk_machine_seq CHECK (station_sequence >= 1),
    CONSTRAINT chk_machine_status CHECK (status IN ('ACTIVE', 'MAINTENANCE', 'DECOMMISSIONED')),
    INDEX idx_machine_line (line_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 4. Table: product
-- Purpose: Manufactured finished goods and work-in-progress SKU master catalog.
-- ------------------------------------------------------------------------------
DROP TABLE IF EXISTS product;

CREATE TABLE product (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_code VARCHAR(30) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_category VARCHAR(50) NOT NULL,
    unit_of_measure VARCHAR(20) NOT NULL DEFAULT 'EACH',
    target_pack_size INT UNSIGNED NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_product PRIMARY KEY (id),
    CONSTRAINT uk_product_code UNIQUE (product_code),
    CONSTRAINT chk_product_pack_size CHECK (target_pack_size >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 5. Table: shift
-- Purpose: Abstract recurring 8-hour shift templates.
-- ------------------------------------------------------------------------------
DROP TABLE IF EXISTS shift;

CREATE TABLE shift (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    shift_code VARCHAR(10) NOT NULL,
    shift_name VARCHAR(50) NOT NULL,
    default_start_time TIME NOT NULL,
    default_end_time TIME NOT NULL,
    spans_midnight BOOLEAN NOT NULL DEFAULT FALSE,
    gross_duration_minutes INT UNSIGNED NOT NULL DEFAULT 480,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_shift PRIMARY KEY (id),
    CONSTRAINT uk_shift_code UNIQUE (shift_code),
    CONSTRAINT chk_shift_duration CHECK (gross_duration_minutes = 480)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 6. Table: downtime_reason
-- Purpose: Standardized root-cause taxonomy for equipment and operational stops.
-- ------------------------------------------------------------------------------
DROP TABLE IF EXISTS downtime_reason;

CREATE TABLE downtime_reason (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    reason_code VARCHAR(30) NOT NULL,
    category VARCHAR(30) NOT NULL,
    subcategory VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    default_is_planned BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_downtime_reason PRIMARY KEY (id),
    CONSTRAINT uk_downtime_reason_code UNIQUE (reason_code),
    CONSTRAINT chk_dtr_category CHECK (category IN (
        'EQUIPMENT_FAILURE',
        'MATERIAL_SHORTAGE',
        'OPERATIONAL_DELAY',
        'PLANNED_MAINTENANCE',
        'CHANGEOVER',
        'PLANNED_BREAK'
    )),
    INDEX idx_dtr_category (category, subcategory)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
