package com.manufacturing.oee.machinestate.event.entity;

import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.Instant;

/**
 * JPA entity representing a machine operational state transition event.
 * Maps to table: `state_event`
 */
@Entity
@Table(name = "state_event")
public class StateEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_code", nullable = false, unique = true, length = 50)
    private String eventCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id")
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_instance_id")
    private ShiftInstance shiftInstance;

    @Column(name = "previous_state", length = 30)
    private String previousState;

    @Column(name = "state", nullable = false, length = 30)
    private String state;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downtime_reason_id")
    private DowntimeReason downtimeReason;

    @Column(name = "source", nullable = false, length = 50)
    private String source = "PLC";

    @Column(name = "is_quarantined", nullable = false)
    private Boolean isQuarantined = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public StateEvent() {
    }

    public StateEvent(String eventCode, Machine machine, ProductionOrder productionOrder,
                      ShiftInstance shiftInstance, String previousState, String state,
                      Instant startTime, Instant endTime, DowntimeReason downtimeReason,
                      String source) {
        this.eventCode = eventCode;
        this.machine = machine;
        this.productionOrder = productionOrder;
        this.shiftInstance = shiftInstance;
        this.previousState = previousState;
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.durationSeconds = (int) Duration.between(startTime, endTime).getSeconds();
        }
        this.downtimeReason = downtimeReason;
        this.source = source != null ? source : "PLC";
        this.isQuarantined = false;
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

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
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

    public String getPreviousState() {
        return previousState;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public DowntimeReason getDowntimeReason() {
        return downtimeReason;
    }

    public void setDowntimeReason(DowntimeReason downtimeReason) {
        this.downtimeReason = downtimeReason;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getIsQuarantined() {
        return isQuarantined;
    }

    public void setIsQuarantined(Boolean quarantined) {
        isQuarantined = quarantined;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
