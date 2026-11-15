package com.manufacturing.oee.anomaly.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing strictly isolated ground-truth synthetic anomaly injections.
 * Maps to table: `anomaly_ground_truth`
 */
@Entity
@Table(name = "anomaly_ground_truth")
public class AnomalyGroundTruth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "injection_uuid", nullable = false, unique = true, length = 36)
    private String injectionUuid;

    @Column(name = "anomaly_code", nullable = false, length = 50)
    private String anomalyCode;

    @Column(name = "entity_name", nullable = false, length = 50)
    private String entityName;

    @Column(name = "target_record_id", nullable = false)
    private Long targetRecordId;

    @Column(name = "injected_value_json", nullable = false, columnDefinition = "json")
    private String injectedValueJson;

    @Column(name = "baseline_value_json", nullable = false, columnDefinition = "json")
    private String baselineValueJson;

    @Column(name = "injection_timestamp", nullable = false)
    private Instant injectionTimestamp;

    @Column(name = "generation_seed", nullable = false)
    private Long generationSeed = 20261022L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AnomalyGroundTruth() {
    }

    public AnomalyGroundTruth(String anomalyCode, String entityName, Long targetRecordId,
                              String injectedValueJson, String baselineValueJson, Long generationSeed) {
        this.injectionUuid = UUID.randomUUID().toString();
        this.anomalyCode = anomalyCode;
        this.entityName = entityName;
        this.targetRecordId = targetRecordId;
        this.injectedValueJson = injectedValueJson;
        this.baselineValueJson = baselineValueJson;
        this.generationSeed = generationSeed != null ? generationSeed : 20261022L;
        this.injectionTimestamp = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.injectionUuid == null) {
            this.injectionUuid = UUID.randomUUID().toString();
        }
        if (this.injectionTimestamp == null) {
            this.injectionTimestamp = Instant.now();
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

    public String getInjectionUuid() {
        return injectionUuid;
    }

    public void setInjectionUuid(String injectionUuid) {
        this.injectionUuid = injectionUuid;
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

    public Long getTargetRecordId() {
        return targetRecordId;
    }

    public void setTargetRecordId(Long targetRecordId) {
        this.targetRecordId = targetRecordId;
    }

    public String getInjectedValueJson() {
        return injectedValueJson;
    }

    public void setInjectedValueJson(String injectedValueJson) {
        this.injectedValueJson = injectedValueJson;
    }

    public String getBaselineValueJson() {
        return baselineValueJson;
    }

    public void setBaselineValueJson(String baselineValueJson) {
        this.baselineValueJson = baselineValueJson;
    }

    public Instant getInjectionTimestamp() {
        return injectionTimestamp;
    }

    public void setInjectionTimestamp(Instant injectionTimestamp) {
        this.injectionTimestamp = injectionTimestamp;
    }

    public Long getGenerationSeed() {
        return generationSeed;
    }

    public void setGenerationSeed(Long generationSeed) {
        this.generationSeed = generationSeed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
