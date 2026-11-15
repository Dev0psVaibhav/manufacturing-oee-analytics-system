package com.manufacturing.oee.masterdata.shift.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.masterdata.shift.dto.ShiftInstanceCreateRequest;
import com.manufacturing.oee.masterdata.shift.dto.ShiftInstanceResponse;
import com.manufacturing.oee.masterdata.shift.service.ShiftInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for Shift Instance operational calendar records.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/shifts/instances")
@Tag(name = "Shift Instances", description = "Endpoints for scheduling concrete calendar shift instances and overnight shifts")
public class ShiftInstanceController {

    private final ShiftInstanceService shiftInstanceService;

    public ShiftInstanceController(ShiftInstanceService shiftInstanceService) {
        this.shiftInstanceService = shiftInstanceService;
    }

    @PostMapping
    @Operation(summary = "Schedule shift instance", description = "Materializes a shift template occurrence on an operational calendar date.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Shift instance created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid timestamps or duplicate shift for date"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate shift instance code")
    })
    public ResponseEntity<ApiResponse<ShiftInstanceResponse>> createShiftInstance(@Valid @RequestBody ShiftInstanceCreateRequest request) {
        ShiftInstanceResponse created = shiftInstanceService.createShiftInstance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shift instance created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get shift instances", description = "Retrieves all shift instances or filters by operational calendar date.")
    public ResponseEntity<ApiResponse<List<ShiftInstanceResponse>>> getShiftInstances(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ShiftInstanceResponse> instances = date != null
                ? shiftInstanceService.getShiftInstancesByDate(date)
                : shiftInstanceService.getAllShiftInstances();
        return ResponseEntity.ok(ApiResponse.success(instances));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shift instance by ID", description = "Retrieves shift instance details by database surrogate key.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shift instance found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shift instance not found")
    })
    public ResponseEntity<ApiResponse<ShiftInstanceResponse>> getShiftInstanceById(@PathVariable Long id) {
        ShiftInstanceResponse instance = shiftInstanceService.getShiftInstanceById(id);
        return ResponseEntity.ok(ApiResponse.success(instance));
    }
}
