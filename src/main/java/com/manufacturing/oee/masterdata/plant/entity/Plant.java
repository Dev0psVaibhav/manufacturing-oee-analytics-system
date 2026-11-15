package com.manufacturing.oee.masterdata.plant.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing the top-level manufacturing facility master record.
 * Maps to table: `plant`
 */
@Entity
@Table(name = "plant")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_code", nullable = false, unique = true, length = 20)
    private String plantCode;

    @Column(name = "plant_name", nullable = false, length = 100)
    private String plantName;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone = "UTC";

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode = "USA";

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Plant() {
    }

    public Plant(String plantCode, String plantName, String timeZone, String countryCode, String status) {
        this.plantCode = plantCode;
        this.plantName = plantName;
        this.timeZone = timeZone != null ? timeZone : "UTC";
        this.countryCode = countryCode != null ? countryCode : "USA";
        this.status = status != null ? status : "ACTIVE";
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

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
