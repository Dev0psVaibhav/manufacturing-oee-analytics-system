package com.manufacturing.oee.masterdata.downtime.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonCreateRequest;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonResponse;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonUpdateRequest;
import com.manufacturing.oee.masterdata.downtime.service.DowntimeReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Downtime Reason taxonomy master data operations.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/downtime-reasons")
@Tag(name = "Downtime Reason Master Data", description = "Endpoints for managing standardized downtime root cause reasons and loss categories")
public class DowntimeReasonController {

    private final DowntimeReasonService reasonService;

    public DowntimeReasonController(DowntimeReasonService reasonService) {
        this.reasonService = reasonService;
    }

    @PostMapping
    @Operation(summary = "Create downtime reason", description = "Registers a new standardized downtime root-cause reason code.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reason created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate reason code")
    })
    public ResponseEntity<ApiResponse<DowntimeReasonResponse>> createReason(@Valid @RequestBody DowntimeReasonCreateRequest request) {
        DowntimeReasonResponse created = reasonService.createReason(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Downtime reason created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get all downtime reasons", description = "Retrieves downtime reason taxonomy, optionally filtered by category or active status.")
    public ResponseEntity<ApiResponse<List<DowntimeReasonResponse>>> getAllReasons(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        List<DowntimeReasonResponse> reasons = reasonService.getAllReasons(category, activeOnly);
        return ResponseEntity.ok(ApiResponse.success(reasons));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get downtime reason by ID", description = "Retrieves downtime reason details by database ID.")
    public ResponseEntity<ApiResponse<DowntimeReasonResponse>> getReasonById(@PathVariable Long id) {
        DowntimeReasonResponse reason = reasonService.getReasonById(id);
        return ResponseEntity.ok(ApiResponse.success(reason));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update downtime reason", description = "Updates category, description, or status of an existing reason code.")
    public ResponseEntity<ApiResponse<DowntimeReasonResponse>> updateReason(
            @PathVariable Long id,
            @Valid @RequestBody DowntimeReasonUpdateRequest request) {
        DowntimeReasonResponse updated = reasonService.updateReason(id, request);
        return ResponseEntity.ok(ApiResponse.success("Downtime reason updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate downtime reason", description = "Soft-deactivates downtime reason (sets isActive to false).")
    public ResponseEntity<Void> deactivateReason(@PathVariable Long id) {
        reasonService.deactivateReason(id);
        return ResponseEntity.noContent().build();
    }
}
