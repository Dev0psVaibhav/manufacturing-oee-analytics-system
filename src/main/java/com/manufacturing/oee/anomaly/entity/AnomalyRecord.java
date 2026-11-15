package com.manufacturing.oee.anomaly.entity;

import com.manufacturing.oee.masterdata.machine.entity.Machine;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing an operational manufacturing anomaly detected by rule-based engines.
 * Maps to table: `anomaly_record`
 */
@Entity
@Table(name = "anomaly_record")
public class AnomalyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anomaly_uuid", nullable = false, unique = true, length = 36)
    private String anomalyUuid;

    @Column(name = "anomaly_code", nullable = false, length = 50)
    private String anomalyCode;

    @Column(name = "entity_name", nullable = false, length = 50)
    private String entityName;

    @Column(name = "primary_record_id", nullable = false)
    private Long primaryRecordId;

    @Column(name = "secondary_record_id")
    private Long secondaryRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @Column(name = "variance_magnitude", precision = 12, scale = 4)
    private BigDecimal varianceMagnitude;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "detection_timestamp", nullable = false)
    private Instant detectionTimestamp;

    @Column(name = "resolution_status", nullable = false, length = 20)
    private String resolutionStatus = "OPEN";

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AnomalyRecord() {
    }

    public AnomalyRecord(String anomalyCode, String entityName, Long primaryRecordId,
                         Long secondaryRecordId, Machine machine, BigDecimal varianceMagnitude,
                         String severity, String description) {
        this.anomalyUuid = UUID.randomUUID().toString();
        this.anomalyCode = anomalyCode;
        this.entityName = entityName;
        this.primaryRecordId = primaryRecordId;
        this.secondaryRecordId = secondaryRecordId;
        this.machine = machine;
        this.varianceMagnitude = varianceMagnitude;
        this.severity = severity != null ? severity : "WARNING";
        this.detectionTimestamp = Instant.now();
        this.resolutionStatus = "OPEN";
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        if (this.anomalyUuid == null) {
            this.anomalyUuid = UUID.randomUUID().toString();
        }
        if (this.detectionTimestamp == null) {
            this.detectionTimestamp = Instant.now();
        }
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnomalyUuid() {
        return anomalyUuid;
    }

    public void setAnomalyUuid(String anomalyUuid) {
        this.anomalyUuid = anomalyUuid;
    }

    public String getAnomalyCode() {
        return anomalyCode;
    }

    public void setAnomalyCode(String anomalyCode) {
        this.anomalyCode = anomalyCode;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getPrimaryRecordId() {
        return primaryRecordId;
    }

    public void setPrimaryRecordId(Long primaryRecordId) {
        this.primaryRecordId = primaryRecordId;
    }

    public Long getSecondaryRecordId() {
        return secondaryRecordId;
    }

    public void setSecondaryRecordId(Long secondaryRecordId) {
        this.secondaryRecordId = secondaryRecordId;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public BigDecimal getVarianceMagnitude() {
        return varianceMagnitude;
    }

    public void setVarianceMagnitude(BigDecimal varianceMagnitude) {
        this.varianceMagnitude = varianceMagnitude;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Instant getDetectionTimestamp() {
        return detectionTimestamp;
    }

    public void setDetectionTimestamp(Instant detectionTimestamp) {
        this.detectionTimestamp = detectionTimestamp;
    }

    public String getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(String resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
