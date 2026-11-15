package com.manufacturing.oee.masterdata.line.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.masterdata.line.dto.LineCreateRequest;
import com.manufacturing.oee.masterdata.line.dto.LineResponse;
import com.manufacturing.oee.masterdata.line.dto.LineUpdateRequest;
import com.manufacturing.oee.masterdata.line.service.LineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Production Line master data operations.
 */
@RestController
@Tag(name = "Production Line Master Data", description = "Endpoints for managing production line master records")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(AppConstants.API_V1_PREFIX + "/lines")
    @Operation(summary = "Create a new production line", description = "Creates a new production line under an existing plant.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Line created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent plant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Line code already exists in plant")
    })
    public ResponseEntity<ApiResponse<LineResponse>> createLine(@Valid @RequestBody LineCreateRequest request) {
        LineResponse created = lineService.createLine(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Production line created successfully", created));
    }

    @GetMapping(AppConstants.API_V1_PREFIX + "/lines")
    @Operation(summary = "Get all production lines", description = "Retrieves a list of all configured production lines.")
    public ResponseEntity<ApiResponse<List<LineResponse>>> getAllLines() {
        List<LineResponse> lines = lineService.getAllLines();
        return ResponseEntity.ok(ApiResponse.success(lines));
    }

    @GetMapping(AppConstants.API_V1_PREFIX + "/lines/{id}")
    @Operation(summary = "Get production line by ID", description = "Retrieves production line details by database surrogate key.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Line found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Line not found")
    })
    public ResponseEntity<ApiResponse<LineResponse>> getLineById(@PathVariable Long id) {
        LineResponse line = lineService.getLineById(id);
        return ResponseEntity.ok(ApiResponse.success(line));
    }

    @GetMapping(AppConstants.API_V1_PREFIX + "/plants/{plantId}/lines")
    @Operation(summary = "Get lines by plant", description = "Retrieves all production lines belonging to a given plant.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lines retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plant not found")
    })
    public ResponseEntity<ApiResponse<List<LineResponse>>> getLinesByPlant(@PathVariable Long plantId) {
        List<LineResponse> lines = lineService.getLinesByPlantId(plantId);
        return ResponseEntity.ok(ApiResponse.success(lines));
    }

    @PutMapping(AppConstants.API_V1_PREFIX + "/lines/{id}")
    @Operation(summary = "Update production line details", description = "Updates mutable properties of an existing production line.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Line updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Line not found")
    })
    public ResponseEntity<ApiResponse<LineResponse>> updateLine(
            @PathVariable Long id,
            @Valid @RequestBody LineUpdateRequest request) {
        LineResponse updated = lineService.updateLine(id, request);
        return ResponseEntity.ok(ApiResponse.success("Production line updated successfully", updated));
    }

    @DeleteMapping(AppConstants.API_V1_PREFIX + "/lines/{id}")
    @Operation(summary = "Deactivate production line", description = "Soft-deactivates an existing production line (sets isActive to false).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Line deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Line not found")
    })
    public ResponseEntity<Void> deactivateLine(@PathVariable Long id) {
        lineService.deactivateLine(id);
        return ResponseEntity.noContent().build();
    }
}
