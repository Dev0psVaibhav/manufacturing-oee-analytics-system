package com.manufacturing.oee.production.record.entity;

import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.product.entity.Product;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing a manufacturing production quantity record.
 * Maps to table: `production_record`
 */
@Entity
@Table(name = "production_record")
public class ProductionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_code", nullable = false, unique = true, length = 50)
    private String recordCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private ProductionLine line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_instance_id", nullable = false)
    private ShiftInstance shiftInstance;

    @Column(name = "production_timestamp", nullable = false)
    private Instant productionTimestamp;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "good_quantity", nullable = false)
    private Integer goodQuantity;

    @Column(name = "rejected_quantity", nullable = false)
    private Integer rejectedQuantity;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ProductionRecord() {
    }

    public ProductionRecord(String recordCode, ProductionOrder productionOrder, Product product,
                            Plant plant, ProductionLine line, Machine machine,
                            ShiftInstance shiftInstance, Instant productionTimestamp,
                            Integer totalQuantity, Integer goodQuantity, Integer rejectedQuantity,
                            String notes) {
        this.recordCode = recordCode;
        this.productionOrder = productionOrder;
        this.product = product;
        this.plant = plant;
        this.line = line;
        this.machine = machine;
        this.shiftInstance = shiftInstance;
        this.productionTimestamp = productionTimestamp;
        this.totalQuantity = totalQuantity;
        this.goodQuantity = goodQuantity;
        this.rejectedQuantity = rejectedQuantity;
        this.notes = notes;
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

    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public ProductionOrder getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        this.productionOrder = productionOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public ProductionLine getLine() {
        return line;
    }

    public void setLine(ProductionLine line) {
        this.line = line;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public ShiftInstance getShiftInstance() {
        return shiftInstance;
    }

    public void setShiftInstance(ShiftInstance shiftInstance) {
        this.shiftInstance = shiftInstance;
    }

    public Instant getProductionTimestamp() {
        return productionTimestamp;
    }

    public void setProductionTimestamp(Instant productionTimestamp) {
        this.productionTimestamp = productionTimestamp;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getGoodQuantity() {
        return goodQuantity;
    }

    public void setGoodQuantity(Integer goodQuantity) {
        this.goodQuantity = goodQuantity;
    }

    public Integer getRejectedQuantity() {
        return rejectedQuantity;
    }

    public void setRejectedQuantity(Integer rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
