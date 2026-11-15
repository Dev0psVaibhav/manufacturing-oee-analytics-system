package com.manufacturing.oee.masterdata.machine.entity;

import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing a physical machine on a production line.
 * Maps to table: `machine`
 */
@Entity
@Table(name = "machine")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private ProductionLine line;

    @Column(name = "machine_code", nullable = false, unique = true, length = 30)
    private String machineCode;

    @Column(name = "machine_name", nullable = false, length = 100)
    private String machineName;

    @Column(name = "station_sequence", nullable = false)
    private Integer stationSequence;

    @Column(name = "asset_tag", length = 50)
    private String assetTag;

    @Column(name = "is_pacing_machine", nullable = false)
    private Boolean isPacingMachine = false;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Machine() {
    }

    public Machine(ProductionLine line, String machineCode, String machineName,
                   Integer stationSequence, String assetTag,
                   Boolean isPacingMachine, String status) {
        this.line = line;
        this.machineCode = machineCode;
        this.machineName = machineName;
        this.stationSequence = stationSequence;
        this.assetTag = assetTag;
        this.isPacingMachine = isPacingMachine != null ? isPacingMachine : false;
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

    public ProductionLine getLine() {
        return line;
    }

    public void setLine(ProductionLine line) {
        this.line = line;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Integer getStationSequence() {
        return stationSequence;
    }

    public void setStationSequence(Integer stationSequence) {
        this.stationSequence = stationSequence;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public Boolean getIsPacingMachine() {
        return isPacingMachine;
    }

    public void setIsPacingMachine(Boolean isPacingMachine) {
        this.isPacingMachine = isPacingMachine;
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
