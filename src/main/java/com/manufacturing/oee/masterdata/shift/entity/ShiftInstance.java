package com.manufacturing.oee.masterdata.shift.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity representing a concrete scheduled occurrence of a Shift on a specific operational date.
 * Maps to table: `shift_instance`
 */
@Entity
@Table(name = "shift_instance")
public class ShiftInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shift_instance_code", nullable = false, unique = true, length = 50)
    private String shiftInstanceCode;

    @Column(name = "operational_date", nullable = false)
    private LocalDate operationalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "start_timestamp", nullable = false)
    private Instant startTimestamp;

    @Column(name = "end_timestamp", nullable = false)
    private Instant endTimestamp;

    @Column(name = "gross_duration_minutes", nullable = false)
    private Integer grossDurationMinutes = 480;

    @Column(name = "planned_break_minutes", nullable = false)
    private Integer plannedBreakMinutes = 60;

    @Column(name = "net_planned_production_minutes", nullable = false)
    private Integer netPlannedProductionMinutes = 420;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ShiftInstance() {
    }

    public ShiftInstance(String shiftInstanceCode, LocalDate operationalDate, Shift shift,
                         Instant startTimestamp, Instant endTimestamp,
                         Integer grossDurationMinutes, Integer plannedBreakMinutes,
                         Integer netPlannedProductionMinutes) {
        this.shiftInstanceCode = shiftInstanceCode;
        this.operationalDate = operationalDate;
        this.shift = shift;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.grossDurationMinutes = grossDurationMinutes != null ? grossDurationMinutes : 480;
        this.plannedBreakMinutes = plannedBreakMinutes != null ? plannedBreakMinutes : 60;
        this.netPlannedProductionMinutes = netPlannedProductionMinutes != null ? netPlannedProductionMinutes : 420;
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

    public String getShiftInstanceCode() {
        return shiftInstanceCode;
    }

    public void setShiftInstanceCode(String shiftInstanceCode) {
        this.shiftInstanceCode = shiftInstanceCode;
    }

    public LocalDate getOperationalDate() {
        return operationalDate;
    }

    public void setOperationalDate(LocalDate operationalDate) {
        this.operationalDate = operationalDate;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Instant getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Instant startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Instant getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Instant endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Integer getGrossDurationMinutes() {
        return grossDurationMinutes;
    }

    public void setGrossDurationMinutes(Integer grossDurationMinutes) {
        this.grossDurationMinutes = grossDurationMinutes;
    }

    public Integer getPlannedBreakMinutes() {
        return plannedBreakMinutes;
    }

    public void setPlannedBreakMinutes(Integer plannedBreakMinutes) {
        this.plannedBreakMinutes = plannedBreakMinutes;
    }

    public Integer getNetPlannedProductionMinutes() {
        return netPlannedProductionMinutes;
    }

    public void setNetPlannedProductionMinutes(Integer netPlannedProductionMinutes) {
        this.netPlannedProductionMinutes = netPlannedProductionMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
