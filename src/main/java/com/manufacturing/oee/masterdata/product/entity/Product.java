package com.manufacturing.oee.masterdata.product.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing manufactured finished goods and work-in-progress SKU master records.
 * Maps to table: `product`
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, length = 30)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_category", nullable = false, length = 50)
    private String productCategory;

    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private String unitOfMeasure = "EACH";

    @Column(name = "target_pack_size", nullable = false)
    private Integer targetPackSize = 1;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Product() {
    }

    public Product(String productCode, String productName, String productCategory, String unitOfMeasure, Integer targetPackSize, Boolean isActive) {
        this.productCode = productCode;
        this.productName = productName;
        this.productCategory = productCategory;
        this.unitOfMeasure = unitOfMeasure != null ? unitOfMeasure : "EACH";
        this.targetPackSize = targetPackSize != null ? targetPackSize : 1;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getTargetPackSize() {
        return targetPackSize;
    }

    public void setTargetPackSize(Integer targetPackSize) {
        this.targetPackSize = targetPackSize;
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
