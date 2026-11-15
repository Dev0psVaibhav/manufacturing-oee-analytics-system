package com.manufacturing.oee.masterdata.shift.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalTime;

/**
 * JPA entity representing abstract recurring 8-hour shift templates.
 * Maps to table: `shift`
 */
@Entity
@Table(name = "shift")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shift_code", nullable = false, unique = true, length = 10)
    private String shiftCode;

    @Column(name = "shift_name", nullable = false, length = 50)
    private String shiftName;

    @Column(name = "default_start_time", nullable = false)
    private LocalTime defaultStartTime;

    @Column(name = "default_end_time", nullable = false)
    private LocalTime defaultEndTime;

    @Column(name = "spans_midnight", nullable = false)
    private Boolean spansMidnight = false;

    @Column(name = "gross_duration_minutes", nullable = false)
    private Integer grossDurationMinutes = 480;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Shift() {
    }

    public Shift(String shiftCode, String shiftName, LocalTime defaultStartTime, LocalTime defaultEndTime, Boolean spansMidnight) {
        this.shiftCode = shiftCode;
        this.shiftName = shiftName;
        this.defaultStartTime = defaultStartTime;
        this.defaultEndTime = defaultEndTime;
        this.spansMidnight = spansMidnight != null ? spansMidnight : false;
        this.grossDurationMinutes = 480;
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

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public LocalTime getDefaultStartTime() {
        return defaultStartTime;
    }

    public void setDefaultStartTime(LocalTime defaultStartTime) {
        this.defaultStartTime = defaultStartTime;
    }

    public LocalTime getDefaultEndTime() {
        return defaultEndTime;
    }

    public void setDefaultEndTime(LocalTime defaultEndTime) {
        this.defaultEndTime = defaultEndTime;
    }

    public Boolean getSpansMidnight() {
        return spansMidnight;
    }

    public void setSpansMidnight(Boolean spansMidnight) {
        this.spansMidnight = spansMidnight;
    }

    public Integer getGrossDurationMinutes() {
        return grossDurationMinutes;
    }

    public void setGrossDurationMinutes(Integer grossDurationMinutes) {
        this.grossDurationMinutes = grossDurationMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
