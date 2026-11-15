package com.manufacturing.oee.masterdata.downtime.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing standardized root-cause taxonomy for equipment and operational stops.
 * Maps to table: `downtime_reason`
 */
@Entity
@Table(name = "downtime_reason")
public class DowntimeReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason_code", nullable = false, unique = true, length = 30)
    private String reasonCode;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "subcategory", nullable = false, length = 50)
    private String subcategory;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "default_is_planned", nullable = false)
    private Boolean defaultIsPlanned = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DowntimeReason() {
    }

    public DowntimeReason(String reasonCode, String category, String subcategory, String description, Boolean defaultIsPlanned, Boolean isActive) {
        this.reasonCode = reasonCode;
        this.category = category;
        this.subcategory = subcategory;
        this.description = description;
        this.defaultIsPlanned = defaultIsPlanned != null ? defaultIsPlanned : false;
        this.isActive = isActive != null ? isActive : true;
    }

    @PrePersist
    protected void onCreate() {
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

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDefaultIsPlanned() {
        return defaultIsPlanned;
    }

    public void setDefaultIsPlanned(Boolean defaultIsPlanned) {
        this.defaultIsPlanned = defaultIsPlanned;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
