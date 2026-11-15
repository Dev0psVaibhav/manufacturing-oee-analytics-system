package com.manufacturing.oee.masterdata.machine.entity;

import com.manufacturing.oee.masterdata.product.entity.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity representing authoritative engineering calibration of Ideal Cycle Time (ICT).
 * Maps to table: `machine_product_config`
 */
@Entity
@Table(name = "machine_product_config")
public class MachineProductConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "ideal_cycle_time_seconds", nullable = false, precision = 10, scale = 4)
    private BigDecimal idealCycleTimeSeconds;

    @Column(name = "design_speed_units_per_minute", nullable = false, precision = 10, scale = 2)
    private BigDecimal designSpeedUnitsPerMinute;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public MachineProductConfig() {
    }

    public MachineProductConfig(Machine machine, Product product, BigDecimal idealCycleTimeSeconds,
                                BigDecimal designSpeedUnitsPerMinute, Instant effectiveFrom,
                                Instant effectiveTo, Boolean isActive) {
        this.machine = machine;
        this.product = product;
        this.idealCycleTimeSeconds = idealCycleTimeSeconds;
        this.designSpeedUnitsPerMinute = designSpeedUnitsPerMinute;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
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

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getIdealCycleTimeSeconds() {
        return idealCycleTimeSeconds;
    }

    public void setIdealCycleTimeSeconds(BigDecimal idealCycleTimeSeconds) {
        this.idealCycleTimeSeconds = idealCycleTimeSeconds;
    }

    public BigDecimal getDesignSpeedUnitsPerMinute() {
        return designSpeedUnitsPerMinute;
    }

    public void setDesignSpeedUnitsPerMinute(BigDecimal designSpeedUnitsPerMinute) {
        this.designSpeedUnitsPerMinute = designSpeedUnitsPerMinute;
    }

    public Instant getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Instant effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Instant getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Instant effectiveTo) {
        this.effectiveTo = effectiveTo;
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
