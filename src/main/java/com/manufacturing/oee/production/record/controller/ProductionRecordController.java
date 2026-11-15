package com.manufacturing.oee.production.record.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.production.record.dto.ProductionRecordCreateRequest;
import com.manufacturing.oee.production.record.dto.ProductionRecordResponse;
import com.manufacturing.oee.production.record.service.ProductionRecordService;
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
 * REST controller for Production Quantity Telemetry records.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/production-records")
@Tag(name = "Production Records", description = "Endpoints for logging and searching manufacturing production quantities (Total, Good, Rejected)")
public class ProductionRecordController {

    private final ProductionRecordService recordService;

    public ProductionRecordController(ProductionRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    @Operation(summary = "Log production quantity record", description = "Records a new discrete production count pulse with conservation check: Total = Good + Rejected.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Production record created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Quantity conservation violation or invalid references"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate record code")
    })
    public ResponseEntity<ApiResponse<ProductionRecordResponse>> createRecord(@Valid @RequestBody ProductionRecordCreateRequest request) {
        ProductionRecordResponse created = recordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Production record logged successfully", created));
    }

    @GetMapping
    @Operation(summary = "Search production records", description = "Retrieves production records filtered by order, plant, line, machine, product, shift, or date range.")
    public ResponseEntity<ApiResponse<List<ProductionRecordResponse>>> searchRecords(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long plantId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long shiftInstanceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<ProductionRecordResponse> results = recordService.searchRecords(
                orderId, plantId, lineId, machineId, productId, shiftInstanceId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get production record by ID", description = "Retrieves production quantity details by surrogate database ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Record found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Record not found")
    })
    public ResponseEntity<ApiResponse<ProductionRecordResponse>> getRecordById(@PathVariable Long id) {
        ProductionRecordResponse record = recordService.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }
}
