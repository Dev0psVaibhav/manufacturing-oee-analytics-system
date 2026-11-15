package com.manufacturing.oee.machinestate.downtime.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordCreateRequest;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordResponse;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordUpdateRequest;
import com.manufacturing.oee.machinestate.downtime.service.DowntimeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for Equipment Downtime Transactions.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/downtime-records")
@Tag(name = "Downtime Records", description = "Endpoints for initiating, resolving, and querying equipment downtime events")
public class DowntimeRecordController {

    private final DowntimeRecordService downtimeService;

    public DowntimeRecordController(DowntimeRecordService downtimeService) {
        this.downtimeService = downtimeService;
    }

    @PostMapping
    @Operation(summary = "Start or record downtime event", description = "Logs a new equipment downtime incident (OPEN if ongoing, RESOLVED if completed).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Downtime recorded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or inactive downtime reason"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate downtime code")
    })
    public ResponseEntity<ApiResponse<DowntimeRecordResponse>> startDowntime(@Valid @RequestBody DowntimeRecordCreateRequest request) {
        DowntimeRecordResponse created = downtimeService.startDowntime(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Downtime event recorded successfully", created));
    }

    @GetMapping
    @Operation(summary = "Search downtime records", description = "Retrieves downtime incidents filtered by machine, line, status, or date range.")
    public ResponseEntity<ApiResponse<List<DowntimeRecordResponse>>> searchDowntime(
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<DowntimeRecordResponse> results = downtimeService.searchDowntime(machineId, lineId, status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get downtime record by ID", description = "Retrieves downtime record details by surrogate database ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Downtime record found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Downtime record not found")
    })
    public ResponseEntity<ApiResponse<DowntimeRecordResponse>> getDowntimeById(@PathVariable Long id) {
        DowntimeRecordResponse record = downtimeService.getDowntimeById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Resolve downtime event", description = "Concludes an ongoing downtime event by recording end timestamp, computing duration, and updating root cause.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Downtime resolved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "End time precedes start time"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Downtime record not found")
    })
    public ResponseEntity<ApiResponse<DowntimeRecordResponse>> resolveDowntime(
            @PathVariable Long id,
            @Valid @RequestBody DowntimeRecordUpdateRequest request) {
        DowntimeRecordResponse updated = downtimeService.resolveDowntime(id, request);
        return ResponseEntity.ok(ApiResponse.success("Downtime resolved successfully", updated));
    }
}
