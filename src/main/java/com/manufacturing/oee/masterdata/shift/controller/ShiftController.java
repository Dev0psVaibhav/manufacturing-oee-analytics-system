package com.manufacturing.oee.masterdata.shift.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.masterdata.shift.dto.ShiftCreateRequest;
import com.manufacturing.oee.masterdata.shift.dto.ShiftResponse;
import com.manufacturing.oee.masterdata.shift.service.ShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Shift template master data operations.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/shifts")
@Tag(name = "Shift Master Data", description = "Endpoints for managing abstract recurring 8-hour shift templates")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    @Operation(summary = "Create shift template", description = "Defines an abstract recurring 8-hour shift template.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Shift created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate shift code")
    })
    public ResponseEntity<ApiResponse<ShiftResponse>> createShift(@Valid @RequestBody ShiftCreateRequest request) {
        ShiftResponse created = shiftService.createShift(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shift created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get all shift templates", description = "Retrieves all configured shift templates.")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getAllShifts() {
        List<ShiftResponse> shifts = shiftService.getAllShifts();
        return ResponseEntity.ok(ApiResponse.success(shifts));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shift template by ID", description = "Retrieves shift template details by database ID.")
    public ResponseEntity<ApiResponse<ShiftResponse>> getShiftById(@PathVariable Long id) {
        ShiftResponse shift = shiftService.getShiftById(id);
        return ResponseEntity.ok(ApiResponse.success(shift));
    }
}
