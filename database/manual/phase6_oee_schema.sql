-- ==============================================================================
-- Manufacturing OEE Analytics System - Phase 6 OEE Calculation Schema
-- Dialect: MySQL 8.x
-- Storage Engine: InnoDB
-- Character Encoding: utf8mb4
-- Collation: utf8mb4_0900_ai_ci
-- Manual Execution: Run via MySQL Workbench or MySQL CLI
-- Note: Requires Phase 4 and Phase 5 tables.
-- ==============================================================================

USE manufacturing_oee;

-- ------------------------------------------------------------------------------
-- 1. Table: machine_product_config
-- Purpose: Authoritative engineering calibration of Ideal Cycle Times (ICT).
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS machine_product_config (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    machine_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    ideal_cycle_time_seconds DECIMAL(10,4) NOT NULL,
    design_speed_units_per_minute DECIMAL(10,2) NOT NULL,
    effective_from DATETIME(6) NOT NULL,
    effective_to DATETIME(6) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_machine_product_config PRIMARY KEY (id),
    CONSTRAINT fk_mpc_machine FOREIGN KEY (machine_id)
        REFERENCES machine (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_mpc_product FOREIGN KEY (product_id)
        REFERENCES product (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_mpc_cycle_time CHECK (ideal_cycle_time_seconds > 0.0000),
    CONSTRAINT chk_mpc_speed CHECK (design_speed_units_per_minute > 0.00),
    CONSTRAINT chk_mpc_dates CHECK (effective_to IS NULL OR effective_to > effective_from),
    INDEX idx_mpc_lookup (machine_id, product_id, effective_from, effective_to),
    INDEX idx_mpc_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 2. Table: oee_calculation_run
-- Purpose: Audited batch execution runs for OEE metric generation.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS oee_calculation_run (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    run_uuid VARCHAR(36) NOT NULL,
    calculation_timestamp DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    aggregation_level VARCHAR(30) NOT NULL,
    records_processed INT UNSIGNED NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_oee_calculation_run PRIMARY KEY (id),
    CONSTRAINT uk_oee_run_uuid UNIQUE (run_uuid),
    CONSTRAINT chk_oee_run_level CHECK (aggregation_level IN (
        'MACHINE_SHIFT', 'LINE_DATE', 'PLANT_DATE', 'SUMMARY'
    )),
    CONSTRAINT chk_oee_run_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED')),
    INDEX idx_oee_run_date (date_from, date_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 3. Table: oee_aggregate
-- Purpose: Pre-computed, immutable OEE metric snapshots and component ratios.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS oee_aggregate (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    calculation_run_id BIGINT UNSIGNED NOT NULL,
    machine_id BIGINT UNSIGNED NULL,
    line_id BIGINT UNSIGNED NULL,
    plant_id BIGINT UNSIGNED NOT NULL,
    shift_instance_id BIGINT UNSIGNED NULL,
    operational_date DATE NOT NULL,
    planned_production_seconds INT UNSIGNED NOT NULL,
    operating_seconds INT UNSIGNED NOT NULL,
    net_operating_seconds INT UNSIGNED NOT NULL,
    fully_productive_seconds INT UNSIGNED NOT NULL,
    total_parts_produced INT UNSIGNED NOT NULL,
    good_parts_produced INT UNSIGNED NOT NULL,
    availability DECIMAL(7,6) NULL,
    performance DECIMAL(7,6) NULL,
    quality DECIMAL(7,6) NULL,
    oee DECIMAL(7,6) NULL,
    status VARCHAR(25) NOT NULL DEFAULT 'VALID',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_oee_aggregate PRIMARY KEY (id),
    CONSTRAINT fk_oee_agg_run FOREIGN KEY (calculation_run_id)
        REFERENCES oee_calculation_run (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_oee_agg_machine FOREIGN KEY (machine_id)
        REFERENCES machine (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_oee_agg_line FOREIGN KEY (line_id)
        REFERENCES production_line (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_oee_agg_plant FOREIGN KEY (plant_id)
        REFERENCES plant (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_oee_agg_shift FOREIGN KEY (shift_instance_id)
        REFERENCES shift_instance (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_oee_agg_lookup (machine_id, operational_date),
    INDEX idx_oee_agg_line_date (line_id, operational_date),
    INDEX idx_oee_agg_plant_date (plant_id, operational_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
