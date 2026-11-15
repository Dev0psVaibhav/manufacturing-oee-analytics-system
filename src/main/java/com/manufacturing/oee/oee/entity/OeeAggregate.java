package com.manufacturing.oee.oee.entity;

import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity representing pre-computed, immutable OEE metric snapshots and component ratios.
 * Maps to table: `oee_aggregate`
 */
@Entity
@Table(name = "oee_aggregate")
public class OeeAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calculation_run_id", nullable = false)
    private OeeCalculationRun calculationRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private ProductionLine line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_instance_id")
    private ShiftInstance shiftInstance;

    @Column(name = "operational_date", nullable = false)
    private LocalDate operationalDate;

    @Column(name = "planned_production_seconds", nullable = false)
    private Integer plannedProductionSeconds;

    @Column(name = "operating_seconds", nullable = false)
    private Integer operatingSeconds;

    @Column(name = "net_operating_seconds", nullable = false)
    private Integer netOperatingSeconds;

    @Column(name = "fully_productive_seconds", nullable = false)
    private Integer fullyProductiveSeconds;

    @Column(name = "total_parts_produced", nullable = false)
    private Integer totalPartsProduced;

    @Column(name = "good_parts_produced", nullable = false)
    private Integer goodPartsProduced;

    @Column(name = "availability", precision = 7, scale = 6)
    private BigDecimal availability;

    @Column(name = "performance", precision = 7, scale = 6)
    private BigDecimal performance;

    @Column(name = "quality", precision = 7, scale = 6)
    private BigDecimal quality;

    @Column(name = "oee", precision = 7, scale = 6)
    private BigDecimal oee;

    @Column(name = "status", nullable = false, length = 25)
    private String status = "VALID";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public OeeAggregate() {
    }

    public OeeAggregate(OeeCalculationRun calculationRun, Machine machine, ProductionLine line,
                        Plant plant, ShiftInstance shiftInstance, LocalDate operationalDate,
                        Integer plannedProductionSeconds, Integer operatingSeconds,
                        Integer netOperatingSeconds, Integer fullyProductiveSeconds,
                        Integer totalPartsProduced, Integer goodPartsProduced,
                        BigDecimal availability, BigDecimal performance, BigDecimal quality,
                        BigDecimal oee, String status) {
        this.calculationRun = calculationRun;
        this.machine = machine;
        this.line = line;
        this.plant = plant;
        this.shiftInstance = shiftInstance;
        this.operationalDate = operationalDate;
        this.plannedProductionSeconds = plannedProductionSeconds;
        this.operatingSeconds = operatingSeconds;
        this.netOperatingSeconds = netOperatingSeconds;
        this.fullyProductiveSeconds = fullyProductiveSeconds;
        this.totalPartsProduced = totalPartsProduced;
        this.goodPartsProduced = goodPartsProduced;
        this.availability = availability;
        this.performance = performance;
        this.quality = quality;
        this.oee = oee;
        this.status = status != null ? status : "VALID";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OeeCalculationRun getCalculationRun() {
        return calculationRun;
    }

    public void setCalculationRun(OeeCalculationRun calculationRun) {
        this.calculationRun = calculationRun;
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

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public ShiftInstance getShiftInstance() {
        return shiftInstance;
    }

    public void setShiftInstance(ShiftInstance shiftInstance) {
        this.shiftInstance = shiftInstance;
    }

    public LocalDate getOperationalDate() {
        return operationalDate;
    }

    public void setOperationalDate(LocalDate operationalDate) {
        this.operationalDate = operationalDate;
    }

    public Integer getPlannedProductionSeconds() {
        return plannedProductionSeconds;
    }

    public void setPlannedProductionSeconds(Integer plannedProductionSeconds) {
        this.plannedProductionSeconds = plannedProductionSeconds;
    }

    public Integer getOperatingSeconds() {
        return operatingSeconds;
    }

    public void setOperatingSeconds(Integer operatingSeconds) {
        this.operatingSeconds = operatingSeconds;
    }

    public Integer getNetOperatingSeconds() {
        return netOperatingSeconds;
    }

    public void setNetOperatingSeconds(Integer netOperatingSeconds) {
        this.netOperatingSeconds = netOperatingSeconds;
    }

    public Integer getFullyProductiveSeconds() {
        return fullyProductiveSeconds;
    }

    public void setFullyProductiveSeconds(Integer fullyProductiveSeconds) {
        this.fullyProductiveSeconds = fullyProductiveSeconds;
    }

    public Integer getTotalPartsProduced() {
        return totalPartsProduced;
    }

    public void setTotalPartsProduced(Integer totalPartsProduced) {
        this.totalPartsProduced = totalPartsProduced;
    }

    public Integer getGoodPartsProduced() {
        return goodPartsProduced;
    }

    public void setGoodPartsProduced(Integer goodPartsProduced) {
        this.goodPartsProduced = goodPartsProduced;
    }

    public BigDecimal getAvailability() {
        return availability;
    }

    public void setAvailability(BigDecimal availability) {
        this.availability = availability;
    }

    public BigDecimal getPerformance() {
        return performance;
    }

    public void setPerformance(BigDecimal performance) {
        this.performance = performance;
    }

    public BigDecimal getQuality() {
        return quality;
    }

    public void setQuality(BigDecimal quality) {
        this.quality = quality;
    }

    public BigDecimal getOee() {
        return oee;
    }

    public void setOee(BigDecimal oee) {
        this.oee = oee;
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
}
