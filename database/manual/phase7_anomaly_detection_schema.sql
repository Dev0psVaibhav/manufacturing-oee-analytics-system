-- ==============================================================================
-- Manufacturing OEE Analytics System - Phase 7 Anomaly Detection Schema
-- Dialect: MySQL 8.x
-- Storage Engine: InnoDB
-- Character Encoding: utf8mb4
-- Collation: utf8mb4_0900_ai_ci
-- Manual Execution: Run via MySQL Workbench or MySQL CLI
-- Note: Requires Phase 4, Phase 5, and Phase 6 tables.
-- ==============================================================================

USE manufacturing_oee;

-- ------------------------------------------------------------------------------
-- 1. Table: anomaly_record
-- Purpose: Operational anomalies detected by the rule-based detection engine.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS anomaly_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    anomaly_uuid VARCHAR(36) NOT NULL,
    anomaly_code VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    primary_record_id BIGINT UNSIGNED NOT NULL,
    secondary_record_id BIGINT UNSIGNED NULL,
    machine_id BIGINT UNSIGNED NULL,
    variance_magnitude DECIMAL(12,4) NULL,
    severity VARCHAR(20) NOT NULL,
    detection_timestamp DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    resolution_status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    description VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_anomaly_record PRIMARY KEY (id),
    CONSTRAINT uk_anomaly_uuid UNIQUE (anomaly_uuid),
    CONSTRAINT fk_anomaly_machine FOREIGN KEY (machine_id)
        REFERENCES machine (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_anomaly_severity CHECK (severity IN (
        'LOW', 'MEDIUM', 'HIGH', 'WARNING', 'ERROR', 'CRITICAL'
    )),
    CONSTRAINT chk_anomaly_resolution CHECK (resolution_status IN (
        'OPEN', 'ACKNOWLEDGED', 'INVESTIGATING', 'RECONCILED', 'QUARANTINED', 'IGNORED', 'RESOLVED'
    )),
    INDEX idx_anomaly_code (anomaly_code, resolution_status),
    INDEX idx_anomaly_entity (entity_name, primary_record_id),
    INDEX idx_anomaly_machine (machine_id, detection_timestamp),
    INDEX idx_anomaly_detection_time (detection_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------------------------
-- 2. Table: anomaly_ground_truth
-- Purpose: Strictly isolated ground-truth store for synthetic injection benchmarks.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS anomaly_ground_truth (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    injection_uuid VARCHAR(36) NOT NULL,
    anomaly_code VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    target_record_id BIGINT UNSIGNED NOT NULL,
    injected_value_json JSON NOT NULL,
    baseline_value_json JSON NOT NULL,
    injection_timestamp DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    generation_seed BIGINT NOT NULL DEFAULT 20261022,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_anomaly_ground_truth PRIMARY KEY (id),
    CONSTRAINT uk_injection_uuid UNIQUE (injection_uuid),
    INDEX idx_gt_entity (entity_name, target_record_id),
    INDEX idx_gt_code (anomaly_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
