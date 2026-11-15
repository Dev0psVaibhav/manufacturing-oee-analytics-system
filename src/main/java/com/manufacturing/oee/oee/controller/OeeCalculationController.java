package com.manufacturing.oee.oee.controller;

import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.oee.dto.*;
import com.manufacturing.oee.oee.entity.OeeAggregate;
import com.manufacturing.oee.oee.service.OeeAggregationService;
import com.manufacturing.oee.oee.service.OeeCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller exposing endpoints for on-demand OEE calculations,
 * ISO 22400-2 line/plant rollups, and persisted calculation batch runs.
 */
@RestController
@RequestMapping("/api/v1/oee")
@Tag(name = "OEE Calculation Engine", description = "Endpoints for calculating and querying OEE metrics across equipment, lines, plants, and shifts")
public class OeeCalculationController {

    private final OeeCalculationService calculationService;
    private final OeeAggregationService aggregationService;

    public OeeCalculationController(
            OeeCalculationService calculationService,
            OeeAggregationService aggregationService
    ) {
        this.calculationService = calculationService;
        this.aggregationService = aggregationService;
    }

    @GetMapping("/machines/{machineId}")
    @Operation(summary = "Calculate Machine OEE", description = "Calculates on-demand OEE metrics for a specific machine and scheduled shift instance")
    public ResponseEntity<ApiResponse<MachineOeeResponse>> getMachineOee(
            @PathVariable Long machineId,
            @RequestParam Long shiftInstanceId
    ) {
        MachineOeeResponse response = calculationService.calculateMachineOee(machineId, shiftInstanceId);
        return ResponseEntity.ok(ApiResponse.success("Machine OEE calculated successfully", response));
    }

    @GetMapping("/machines/{machineId}/window")
    @Operation(summary = "Calculate Machine OEE for Time Window", description = "Calculates on-demand OEE metrics for a machine within an arbitrary timestamp window")
    public ResponseEntity<ApiResponse<MachineOeeResponse>> getMachineOeeForWindow(
            @PathVariable Long machineId,
            @RequestParam Instant windowStart,
            @RequestParam Instant windowEnd
    ) {
        MachineOeeResponse response = calculationService.calculateMachineOeeForWindow(machineId, windowStart, windowEnd);
        return ResponseEntity.ok(ApiResponse.success("Window OEE calculated successfully", response));
    }

    @GetMapping("/lines/{lineId}")
    @Operation(summary = "Calculate Line OEE", description = "Calculates aggregated Line OEE using ISO 22400-2 Fully Productive Time rollup")
    public ResponseEntity<ApiResponse<LineOeeResponse>> getLineOee(
            @PathVariable Long lineId,
            @RequestParam Long shiftInstanceId
    ) {
        LineOeeResponse response = calculationService.calculateLineOee(lineId, shiftInstanceId);
        return ResponseEntity.ok(ApiResponse.success("Line OEE aggregated successfully", response));
    }

    @GetMapping("/plants/{plantId}")
    @Operation(summary = "Calculate Plant OEE", description = "Calculates aggregated enterprise Plant OEE across all lines for an operational date")
    public ResponseEntity<ApiResponse<PlantOeeResponse>> getPlantOee(
            @PathVariable Long plantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate operationalDate
    ) {
        LocalDate date = operationalDate != null ? operationalDate : LocalDate.now();
        PlantOeeResponse response = calculationService.calculatePlantOee(plantId, date);
        return ResponseEntity.ok(ApiResponse.success("Plant OEE aggregated successfully", response));
    }

    @GetMapping("/shifts/{shiftInstanceId}")
    @Operation(summary = "Calculate Shift OEE", description = "Calculates OEE performance metrics for an entire shift instance across all lines")
    public ResponseEntity<ApiResponse<ShiftOeeResponse>> getShiftOee(
            @PathVariable Long shiftInstanceId
    ) {
        ShiftOeeResponse response = calculationService.calculateShiftOee(shiftInstanceId);
        return ResponseEntity.ok(ApiResponse.success("Shift OEE aggregated successfully", response));
    }

    @PostMapping("/runs")
    @Operation(summary = "Trigger Batch Calculation Run", description = "Executes an OEE calculation batch across requested scope and persists aggregate records")
    public ResponseEntity<ApiResponse<OeeCalculationRunResponse>> executeBatchRun(
            @Valid @RequestBody OeeCalculationRequest request
    ) {
        OeeCalculationRunResponse response = aggregationService.executeAndPersistRun(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Calculation batch run completed", response));
    }

    @GetMapping("/runs")
    @Operation(summary = "List Recent Calculation Runs", description = "Retrieves recent calculation batch execution runs")
    public ResponseEntity<ApiResponse<List<OeeCalculationRunResponse>>> getRecentRuns() {
        List<OeeCalculationRunResponse> response = aggregationService.getRecentRuns();
        return ResponseEntity.ok(ApiResponse.success("Calculation runs retrieved successfully", response));
    }

    @GetMapping("/runs/{runId}")
    @Operation(summary = "Get Calculation Run Details", description = "Retrieves execution details for a specific calculation run")
    public ResponseEntity<ApiResponse<OeeCalculationRunResponse>> getRunById(@PathVariable Long runId) {
        OeeCalculationRunResponse response = aggregationService.getRunById(runId);
        return ResponseEntity.ok(ApiResponse.success("Run details retrieved successfully", response));
    }

    @GetMapping("/runs/{runId}/aggregates")
    @Operation(summary = "Get Persisted Aggregates for Run", description = "Retrieves persisted OEE aggregate records generated by a calculation run")
    public ResponseEntity<ApiResponse<List<OeeAggregateResponse>>> getAggregatesForRun(@PathVariable Long runId) {
        List<OeeAggregateResponse> response = aggregationService.getAggregatesForRun(runId);
        return ResponseEntity.ok(ApiResponse.success("Aggregates retrieved successfully", response));
    }
}
