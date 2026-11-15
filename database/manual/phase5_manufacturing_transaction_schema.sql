-- ==============================================================================
-- Manufacturing OEE Analytics System - Phase 5 Manufacturing Transaction Schema
-- Dialect: MySQL 8.x
-- Storage Engine: InnoDB
-- Character Encoding: utf8mb4
-- Collation: utf8mb4_0900_ai_ci
-- Manual Execution: Run via MySQL Workbench or MySQL CLI
-- Note: Requires Phase 4 master data tables (plant, production_line, machine, product, shift, downtime_reason).
-- ==============================================================================

USE manufacturing_oee;

-- ------------------------------------------------------------------------------
-- 1. Table: shift_instance
-- Purpose: Concrete operational shift occurrences on a calendar date.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shift_instance (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    shift_instance_code VARCHAR(50) NOT NULL,
    operational_date DATE NOT NULL,
    shift_id BIGINT UNSIGNED NOT NULL,
    start_timestamp DATETIME(6) NOT NULL,
    end_timestamp DATETIME(6) NOT NULL,
    gross_duration_minutes INT UNSIGNED NOT NULL DEFAULT 480,
    planned_break_minutes INT UNSIGNED NOT NULL DEFAULT 60,
    net_planned_production_minutes INT UNSIGNED NOT NULL DEFAULT 420,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_shift_instance PRIMARY KEY (id),
    CONSTRAINT fk_shift_instance_shift FOREIGN KEY (shift_id)
        REFERENCES shift (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_shift_instance_code UNIQUE (shift_instance_code),
    CONSTRAINT uk_shift_instance_date UNIQUE (operational_date, shift_id),
    CONSTRAINT chk_shift_inst_times CHECK (end_timestamp > start_timestamp),
    CONSTRAINT chk_shift_inst_ppt CHECK (net_planned_production_minutes = gross_duration_minutes - planned_break_minutes),
    INDEX idx_shift_instance_lookup (operational_date, shift_id),
    INDEX idx_shift_instance_range (start_timestamp, end_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 2. Table: production_order
-- Purpose: Work order lifecycle tracking discrete production runs on a line.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS production_order (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(30) NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    line_id BIGINT UNSIGNED NOT NULL,
    target_quantity INT UNSIGNED NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    scheduled_start DATETIME(6) NOT NULL,
    scheduled_end DATETIME(6) NOT NULL,
    actual_start DATETIME(6) NULL,
    actual_end DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_production_order PRIMARY KEY (id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id)
        REFERENCES product (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_order_line FOREIGN KEY (line_id)
        REFERENCES production_line (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_order_number UNIQUE (order_number),
    CONSTRAINT chk_order_status CHECK (status IN (
        'CREATED', 'RELEASED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'
    )),
    CONSTRAINT chk_order_qty CHECK (target_quantity > 0),
    CONSTRAINT chk_order_sched_dates CHECK (scheduled_end > scheduled_start),
    INDEX idx_order_line_status (line_id, status),
    INDEX idx_order_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 3. Table: production_record
-- Purpose: Manufacturing production quantity records (Total = Good + Rejected).
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS production_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    record_code VARCHAR(50) NOT NULL,
    production_order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    plant_id BIGINT UNSIGNED NOT NULL,
    line_id BIGINT UNSIGNED NOT NULL,
    machine_id BIGINT UNSIGNED NOT NULL,
    shift_instance_id BIGINT UNSIGNED NOT NULL,
    production_timestamp DATETIME(6) NOT NULL,
    total_quantity INT UNSIGNED NOT NULL,
    good_quantity INT UNSIGNED NOT NULL,
    rejected_quantity INT UNSIGNED NOT NULL,
    notes VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_production_record PRIMARY KEY (id),
    CONSTRAINT fk_prod_rec_order FOREIGN KEY (production_order_id)
        REFERENCES production_order (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_prod_rec_product FOREIGN KEY (product_id)
        REFERENCES product (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_prod_rec_plant FOREIGN KEY (plant_id)
        REFERENCES plant (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_prod_rec_line FOREIGN KEY (line_id)
        REFERENCES production_line (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_prod_rec_machine FOREIGN KEY (machine_id)
        REFERENCES machine (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_prod_rec_shift FOREIGN KEY (shift_instance_id)
        REFERENCES shift_instance (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_production_record_code UNIQUE (record_code),
    CONSTRAINT chk_prod_rec_balance CHECK (total_quantity = good_quantity + rejected_quantity),
    INDEX idx_prod_rec_order (production_order_id),
    INDEX idx_prod_rec_machine_time (machine_id, production_timestamp),
    INDEX idx_prod_rec_shift (shift_instance_id),
    INDEX idx_prod_rec_line (line_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 4. Table: state_event
-- Purpose: Machine operating state transition event history.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS state_event (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    event_code VARCHAR(50) NOT NULL,
    machine_id BIGINT UNSIGNED NOT NULL,
    production_order_id BIGINT UNSIGNED NULL,
    shift_instance_id BIGINT UNSIGNED NULL,
    previous_state VARCHAR(30) NULL,
    state VARCHAR(30) NOT NULL,
    start_time DATETIME(6) NOT NULL,
    end_time DATETIME(6) NULL,
    duration_seconds INT UNSIGNED NULL,
    downtime_reason_id BIGINT UNSIGNED NULL,
    source VARCHAR(50) NOT NULL DEFAULT 'PLC',
    is_quarantined BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_state_event PRIMARY KEY (id),
    CONSTRAINT fk_state_event_machine FOREIGN KEY (machine_id)
        REFERENCES machine (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_state_event_order FOREIGN KEY (production_order_id)
        REFERENCES production_order (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_state_event_shift FOREIGN KEY (shift_instance_id)
        REFERENCES shift_instance (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_state_event_reason FOREIGN KEY (downtime_reason_id)
        REFERENCES downtime_reason (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_state_event_code UNIQUE (event_code),
    CONSTRAINT chk_state_event_type CHECK (state IN (
        'RUNNING', 'UNPLANNED_DOWNTIME', 'PLANNED_MAINTENANCE', 'CHANGEOVER', 'PLANNED_BREAK', 'UNSCHEDULED'
    )),
    INDEX idx_state_event_range (machine_id, start_time, end_time),
    INDEX idx_state_event_order (production_order_id),
    INDEX idx_state_event_shift (shift_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 5. Table: downtime_record
-- Purpose: Dedicated downtime event transaction ledger with start/end tracking.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS downtime_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    downtime_code VARCHAR(50) NOT NULL,
    machine_id BIGINT UNSIGNED NOT NULL,
    line_id BIGINT UNSIGNED NOT NULL,
    reason_id BIGINT UNSIGNED NOT NULL,
    production_order_id BIGINT UNSIGNED NULL,
    shift_instance_id BIGINT UNSIGNED NULL,
    start_time DATETIME(6) NOT NULL,
    end_time DATETIME(6) NULL,
    duration_seconds INT UNSIGNED NULL,
    description VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_downtime_record PRIMARY KEY (id),
    CONSTRAINT fk_downtime_record_machine FOREIGN KEY (machine_id)
        REFERENCES machine (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_downtime_record_line FOREIGN KEY (line_id)
        REFERENCES production_line (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_downtime_record_reason FOREIGN KEY (reason_id)
        REFERENCES downtime_reason (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_downtime_record_order FOREIGN KEY (production_order_id)
        REFERENCES production_order (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_downtime_record_shift FOREIGN KEY (shift_instance_id)
        REFERENCES shift_instance (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_downtime_record_code UNIQUE (downtime_code),
    CONSTRAINT chk_downtime_status CHECK (status IN ('OPEN', 'RESOLVED')),
    INDEX idx_downtime_machine_time (machine_id, start_time),
    INDEX idx_downtime_line (line_id),
    INDEX idx_downtime_reason (reason_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
