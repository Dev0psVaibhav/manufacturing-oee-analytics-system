package com.manufacturing.oee.machinestate.downtime.entity;

import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.Instant;

/**
 * JPA entity representing a dedicated equipment downtime transaction record.
 * Maps to table: `downtime_record`
 */
@Entity
@Table(name = "downtime_record")
public class DowntimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "downtime_code", nullable = false, unique = true, length = 50)
    private String downtimeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private ProductionLine line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = false)
    private DowntimeReason reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id")
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_instance_id")
    private ShiftInstance shiftInstance;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DowntimeRecord() {
    }

    public DowntimeRecord(String downtimeCode, Machine machine, ProductionLine line,
                          DowntimeReason reason, ProductionOrder productionOrder,
                          ShiftInstance shiftInstance, Instant startTime, Instant endTime,
                          String description, String status) {
        this.downtimeCode = downtimeCode;
        this.machine = machine;
        this.line = line;
        this.reason = reason;
        this.productionOrder = productionOrder;
        this.shiftInstance = shiftInstance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.status = status != null ? status : "OPEN";
        if (startTime != null && endTime != null) {
            this.durationSeconds = (int) Duration.between(startTime, endTime).getSeconds();
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.startTime != null && this.endTime != null && this.durationSeconds == null) {
            this.durationSeconds = (int) Duration.between(this.startTime, this.endTime).getSeconds();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        if (this.startTime != null && this.endTime != null) {
            this.durationSeconds = (int) Duration.between(this.startTime, this.endTime).getSeconds();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDowntimeCode() {
        return downtimeCode;
    }

    public void setDowntimeCode(String downtimeCode) {
        this.downtimeCode = downtimeCode;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public ProductionLine getLine() {
        return line;
    }

    public void setLine(ProductionLine line) {
        this.line = line;
    }

    public DowntimeReason getReason() {
        return reason;
    }

    public void setReason(DowntimeReason reason) {
        this.reason = reason;
    }

    public ProductionOrder getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        this.productionOrder = productionOrder;
    }

    public ShiftInstance getShiftInstance() {
        return shiftInstance;
    }

    public void setShiftInstance(ShiftInstance shiftInstance) {
        this.shiftInstance = shiftInstance;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
        if (this.startTime != null && endTime != null) {
            this.durationSeconds = (int) Duration.between(this.startTime, endTime).getSeconds();
        }
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
