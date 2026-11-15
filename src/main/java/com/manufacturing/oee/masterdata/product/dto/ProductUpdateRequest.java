package com.manufacturing.oee.masterdata.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing product SKU.
 */
@Schema(description = "Product SKU update payload")
public record ProductUpdateRequest(
        @Schema(description = "Descriptive product SKU name", example = "Front Fender Panel Assembly - Rev B")
        @NotBlank(message = "Product name is required")
        @Size(max = 100, message = "Product name cannot exceed 100 characters")
        String productName,

        @Schema(description = "Product category grouping", example = "Automotive Body Stamping")
        @NotBlank(message = "Product category is required")
        @Size(max = 50, message = "Product category cannot exceed 50 characters")
        String productCategory,

        @Schema(description = "Unit of measure", example = "EACH")
        @Size(max = 20, message = "Unit of measure cannot exceed 20 characters")
        String unitOfMeasure,

        @Schema(description = "Shipping target pack size", example = "1")
        @Positive(message = "Pack size must be positive")
        Integer targetPackSize,

        @Schema(description = "Active status", example = "true")
        Boolean isActive
) {
}
