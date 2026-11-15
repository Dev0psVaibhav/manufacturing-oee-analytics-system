package com.manufacturing.oee.oee.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity tracking execution batch metadata and versions for OEE metric generation.
 * Maps to table: `oee_calculation_run`
 */
@Entity
@Table(name = "oee_calculation_run")
public class OeeCalculationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_uuid", nullable = false, unique = true, length = 36)
    private String runUuid;

    @Column(name = "calculation_timestamp", nullable = false)
    private Instant calculationTimestamp;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @Column(name = "aggregation_level", nullable = false, length = 30)
    private String aggregationLevel;

    @Column(name = "records_processed", nullable = false)
    private Integer recordsProcessed = 0;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "COMPLETED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public OeeCalculationRun() {
    }

    public OeeCalculationRun(String runUuid, LocalDate dateFrom, LocalDate dateTo,
                             String aggregationLevel, Integer recordsProcessed, String status) {
        this.runUuid = runUuid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.aggregationLevel = aggregationLevel;
        this.recordsProcessed = recordsProcessed != null ? recordsProcessed : 0;
        this.status = status != null ? status : "COMPLETED";
    }

    @PrePersist
    protected void onCreate() {
        this.calculationTimestamp = Instant.now();
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

    public String getRunUuid() {
        return runUuid;
    }

    public void setRunUuid(String runUuid) {
        this.runUuid = runUuid;
    }

    public Instant getCalculationTimestamp() {
        return calculationTimestamp;
    }

    public void setCalculationTimestamp(Instant calculationTimestamp) {
        this.calculationTimestamp = calculationTimestamp;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public String getAggregationLevel() {
        return aggregationLevel;
    }

    public void setAggregationLevel(String aggregationLevel) {
        this.aggregationLevel = aggregationLevel;
    }

    public Integer getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(Integer recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
