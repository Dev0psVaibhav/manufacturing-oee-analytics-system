package com.manufacturing.oee.masterdata.plant.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.masterdata.plant.dto.PlantCreateRequest;
import com.manufacturing.oee.masterdata.plant.dto.PlantResponse;
import com.manufacturing.oee.masterdata.plant.dto.PlantUpdateRequest;
import com.manufacturing.oee.masterdata.plant.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Plant master data operations.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/plants")
@Tag(name = "Plant Master Data", description = "Endpoints for managing manufacturing facility master records")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping
    @Operation(summary = "Create a new plant", description = "Creates a new manufacturing plant record with unique site code.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Plant created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Plant code already exists")
    })
    public ResponseEntity<ApiResponse<PlantResponse>> createPlant(@Valid @RequestBody PlantCreateRequest request) {
        PlantResponse created = plantService.createPlant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plant created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get all plants", description = "Retrieves a list of all configured manufacturing plants.")
    public ResponseEntity<ApiResponse<List<PlantResponse>>> getAllPlants() {
        List<PlantResponse> plants = plantService.getAllPlants();
        return ResponseEntity.ok(ApiResponse.success(plants));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plant by ID", description = "Retrieves plant details by database surrogate key.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plant found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plant not found")
    })
    public ResponseEntity<ApiResponse<PlantResponse>> getPlantById(@PathVariable Long id) {
        PlantResponse plant = plantService.getPlantById(id);
        return ResponseEntity.ok(ApiResponse.success(plant));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update plant details", description = "Updates mutable properties of an existing plant record.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plant updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plant not found")
    })
    public ResponseEntity<ApiResponse<PlantResponse>> updatePlant(
            @PathVariable Long id,
            @Valid @RequestBody PlantUpdateRequest request) {
        PlantResponse updated = plantService.updatePlant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Plant updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate plant", description = "Soft-deactivates an existing plant (sets status to INACTIVE) to preserve historical integrity.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Plant deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plant not found")
    })
    public ResponseEntity<Void> deactivatePlant(@PathVariable Long id) {
        plantService.deactivatePlant(id);
        return ResponseEntity.noContent().build();
    }
}
