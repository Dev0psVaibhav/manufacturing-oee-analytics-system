package com.manufacturing.oee.anomaly.controller;

import com.manufacturing.oee.anomaly.dto.AnomalyDetectionRequest;
import com.manufacturing.oee.anomaly.dto.AnomalyResponse;
import com.manufacturing.oee.anomaly.dto.AnomalyStatusUpdateRequest;
import com.manufacturing.oee.anomaly.dto.AnomalySummaryResponse;
import com.manufacturing.oee.anomaly.service.AnomalyDetectionService;
import com.manufacturing.oee.anomaly.service.AnomalyManagementService;
import com.manufacturing.oee.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST controller providing operations for anomaly detection execution,
 * querying detected anomalies, and lifecycle status management.
 */
@RestController
@RequestMapping("/api/v1/anomalies")
@Tag(name = "Anomaly Detection Engine", description = "Endpoints for detecting operational anomalies and managing their resolution lifecycles")
public class AnomalyController {

    private final AnomalyDetectionService detectionService;
    private final AnomalyManagementService managementService;

    public AnomalyController(
            AnomalyDetectionService detectionService,
            AnomalyManagementService managementService
    ) {
        this.detectionService = detectionService;
        this.managementService = managementService;
    }

    @GetMapping
    @Operation(summary = "Search Anomalies", description = "Retrieves paginated and filtered list of detected operational anomalies")
    public ResponseEntity<ApiResponse<Page<AnomalyResponse>>> getAnomalies(
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) String anomalyCode,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String resolutionStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AnomalyResponse> response = managementService.getAnomalies(
                machineId, anomalyCode, severity, resolutionStatus, startDate, endDate, pageable
        );
        return ResponseEntity.ok(ApiResponse.success("Anomalies retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Anomaly by ID", description = "Retrieves a single anomaly record by database identifier")
    public ResponseEntity<ApiResponse<AnomalyResponse>> getAnomalyById(@PathVariable Long id) {
        AnomalyResponse response = managementService.getAnomalyById(id);
        return ResponseEntity.ok(ApiResponse.success("Anomaly retrieved successfully", response));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get Anomaly Summary", description = "Aggregates anomaly metrics by severity, status, and scenario code")
    public ResponseEntity<ApiResponse<AnomalySummaryResponse>> getSummary() {
        AnomalySummaryResponse response = managementService.getSummary();
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved successfully", response));
    }

    @GetMapping("/machines/{machineId}")
    @Operation(summary = "Get Machine Anomalies", description = "Retrieves recent anomalies for a specific machine")
    public ResponseEntity<ApiResponse<List<AnomalyResponse>>> getMachineAnomalies(@PathVariable Long machineId) {
        List<AnomalyResponse> response = managementService.getAnomaliesForMachine(machineId);
        return ResponseEntity.ok(ApiResponse.success("Machine anomalies retrieved successfully", response));
    }

    @PostMapping("/detect")
    @Operation(summary = "Run Anomaly Detection", description = "Executes rule-based anomaly detection on demand across requested scope")
    public ResponseEntity<ApiResponse<List<AnomalyResponse>>> runDetection(
            @Valid @RequestBody AnomalyDetectionRequest request
    ) {
        List<AnomalyResponse> response = detectionService.runDetection(request);
        return ResponseEntity.ok(ApiResponse.success("Anomaly detection completed successfully", response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update Anomaly Status", description = "Updates an anomaly's lifecycle status (e.g., ACKNOWLEDGED, RESOLVED, QUARANTINED)")
    public ResponseEntity<ApiResponse<AnomalyResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AnomalyStatusUpdateRequest request
    ) {
        AnomalyResponse response = managementService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Anomaly status updated successfully", response));
    }

    @PostMapping("/{id}/acknowledge")
    @Operation(summary = "Acknowledge Anomaly", description = "Transitions an anomaly from OPEN to ACKNOWLEDGED")
    public ResponseEntity<ApiResponse<AnomalyResponse>> acknowledge(@PathVariable Long id) {
        AnomalyResponse response = managementService.acknowledge(id);
        return ResponseEntity.ok(ApiResponse.success("Anomaly acknowledged", response));
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolve Anomaly", description = "Transitions an anomaly to RESOLVED status with optional resolution notes")
    public ResponseEntity<ApiResponse<AnomalyResponse>> resolve(
            @PathVariable Long id,
            @RequestParam(required = false) String notes
    ) {
        AnomalyResponse response = managementService.resolve(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Anomaly resolved", response));
    }
}
