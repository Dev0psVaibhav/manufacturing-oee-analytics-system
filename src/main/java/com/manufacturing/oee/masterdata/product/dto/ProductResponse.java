package com.manufacturing.oee.masterdata.product.dto;

import com.manufacturing.oee.masterdata.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a manufactured product SKU.
 */
@Schema(description = "Product SKU response payload")
public record ProductResponse(
        @Schema(description = "Internal database ID", example = "1")
        Long id,

        @Schema(description = "Unique SKU product code", example = "PRD-SKU-001")
        String productCode,

        @Schema(description = "Descriptive product SKU name", example = "Front Fender Panel Assembly")
        String productName,

        @Schema(description = "Product category grouping", example = "Automotive Body Stamping")
        String productCategory,

        @Schema(description = "Unit of measure", example = "EACH")
        String unitOfMeasure,

        @Schema(description = "Shipping target pack size", example = "1")
        Integer targetPackSize,

        @Schema(description = "Active status", example = "true")
        Boolean isActive,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant createdAt,

        @Schema(description = "Update timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant updatedAt
) {
    public static ProductResponse fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductResponse(
                product.getId(),
                product.getProductCode(),
                product.getProductName(),
                product.getProductCategory(),
                product.getUnitOfMeasure(),
                product.getTargetPackSize(),
                product.getIsActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
