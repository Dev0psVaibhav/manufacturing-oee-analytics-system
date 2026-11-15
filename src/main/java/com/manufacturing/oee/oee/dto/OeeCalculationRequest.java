package com.manufacturing.oee.oee.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request payload to trigger an on-demand OEE calculation or persistent batch snapshot.
 */
public record OeeCalculationRequest(
        @NotNull(message = "Calculation scope is required (MACHINE, LINE, PLANT, SHIFT)")
        String scope,

        Long plantId,

        Long lineId,

        Long machineId,

        Long shiftInstanceId,

        LocalDate operationalDate,

        Boolean persistSnapshots
) {
}
