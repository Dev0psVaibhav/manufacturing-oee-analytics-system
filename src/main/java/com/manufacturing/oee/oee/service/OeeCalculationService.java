package com.manufacturing.oee.oee.service;

import com.manufacturing.oee.oee.calculation.model.OeeCalculationResult;
import com.manufacturing.oee.oee.dto.LineOeeResponse;
import com.manufacturing.oee.oee.dto.MachineOeeResponse;
import com.manufacturing.oee.oee.dto.PlantOeeResponse;
import com.manufacturing.oee.oee.dto.ShiftOeeResponse;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Service contract for calculating on-demand OEE metrics at Machine, Line, Plant, and Shift levels.
 */
public interface OeeCalculationService {

    /**
     * Calculates complete OEE metrics for a specific machine and scheduled shift instance.
     */
    MachineOeeResponse calculateMachineOee(Long machineId, Long shiftInstanceId);

    /**
     * Calculates complete OEE metrics for a machine within an arbitrary timestamp window.
     */
    MachineOeeResponse calculateMachineOeeForWindow(Long machineId, Instant windowStart, Instant windowEnd);

    /**
     * Calculates internal raw calculation result for a machine and shift instance.
     */
    OeeCalculationResult calculateMachineOeeResult(Long machineId, Long shiftInstanceId);

    /**
     * Calculates aggregated Line OEE using ISO 22400-2 Fully Productive Time rollup.
     */
    LineOeeResponse calculateLineOee(Long lineId, Long shiftInstanceId);

    /**
     * Calculates aggregated Plant-wide OEE across all production lines for an operational date.
     */
    PlantOeeResponse calculatePlantOee(Long plantId, LocalDate operationalDate);

    /**
     * Calculates aggregate OEE metrics for an entire shift instance across all lines.
     */
    ShiftOeeResponse calculateShiftOee(Long shiftInstanceId);
}
