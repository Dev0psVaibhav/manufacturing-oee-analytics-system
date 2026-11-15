package com.manufacturing.oee.production.record.dto;

import com.manufacturing.oee.production.record.entity.ProductionRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a recorded production quantity record.
 */
@Schema(description = "Production record response payload")
public record ProductionRecordResponse(
        @Schema(description = "Internal database record ID", example = "1001")
        Long id,

        @Schema(description = "Unique telemetry record code", example = "REC-20261022-000001")
        String recordCode,

        @Schema(description = "Production order ID", example = "101")
        Long productionOrderId,

        @Schema(description = "Production order number", example = "PO-2026-000101")
        String orderNumber,

        @Schema(description = "Product SKU ID", example = "1")
        Long productId,

        @Schema(description = "Product SKU code", example = "PRD-SKU-001")
        String productCode,

        @Schema(description = "Plant facility ID", example = "1")
        Long plantId,

        @Schema(description = "Plant code", example = "PLANT-01")
        String plantCode,

        @Schema(description = "Production line ID", example = "1")
        Long lineId,

        @Schema(description = "Line code", example = "LINE-01")
        String lineCode,

        @Schema(description = "Machine ID", example = "1")
        Long machineId,

        @Schema(description = "Machine code", example = "MCH-L01-01")
        String machineCode,

        @Schema(description = "Shift instance ID", example = "1")
        Long shiftInstanceId,

        @Schema(description = "Shift instance code", example = "SHIFT-2026-10-22-S1")
        String shiftInstanceCode,

        @Schema(description = "UTC moment of production record", example = "2026-10-22T08:30:00Z")
        Instant productionTimestamp,

        @Schema(description = "Total parts produced", example = "100")
        Integer totalQuantity,

        @Schema(description = "Conforming parts produced", example = "95")
        Integer goodQuantity,

        @Schema(description = "Defective/rejected parts produced", example = "5")
        Integer rejectedQuantity,

        @Schema(description = "Notes", example = "Standard run")
        String notes,

        @Schema(description = "Record creation timestamp in UTC", example = "2026-10-22T08:31:00Z")
        Instant createdAt
) {
    public static ProductionRecordResponse fromEntity(ProductionRecord record) {
        if (record == null) {
            return null;
        }
        return new ProductionRecordResponse(
                record.getId(),
                record.getRecordCode(),
                record.getProductionOrder() != null ? record.getProductionOrder().getId() : null,
                record.getProductionOrder() != null ? record.getProductionOrder().getOrderNumber() : null,
                record.getProduct() != null ? record.getProduct().getId() : null,
                record.getProduct() != null ? record.getProduct().getProductCode() : null,
                record.getPlant() != null ? record.getPlant().getId() : null,
                record.getPlant() != null ? record.getPlant().getPlantCode() : null,
                record.getLine() != null ? record.getLine().getId() : null,
                record.getLine() != null ? record.getLine().getLineCode() : null,
                record.getMachine() != null ? record.getMachine().getId() : null,
                record.getMachine() != null ? record.getMachine().getMachineCode() : null,
                record.getShiftInstance() != null ? record.getShiftInstance().getId() : null,
                record.getShiftInstance() != null ? record.getShiftInstance().getShiftInstanceCode() : null,
                record.getProductionTimestamp(),
                record.getTotalQuantity(),
                record.getGoodQuantity(),
                record.getRejectedQuantity(),
                record.getNotes(),
                record.getCreatedAt()
        );
    }
}
