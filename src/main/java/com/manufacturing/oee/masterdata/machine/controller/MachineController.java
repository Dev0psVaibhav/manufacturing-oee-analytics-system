package com.manufacturing.oee.masterdata.machine.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.masterdata.machine.dto.MachineCreateRequest;
import com.manufacturing.oee.masterdata.machine.dto.MachineResponse;
import com.manufacturing.oee.masterdata.machine.dto.MachineUpdateRequest;
import com.manufacturing.oee.masterdata.machine.service.MachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Machine equipment asset operations.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX)
@Tag(name = "Machine Master Data", description = "Endpoints for managing workstation machine assets along production lines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping("/machines")
    @Operation(summary = "Create a new machine", description = "Registers a new workstation machine asset assigned to a production line.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Machine created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or station sequence collision"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate machine code")
    })
    public ResponseEntity<ApiResponse<MachineResponse>> createMachine(@Valid @RequestBody MachineCreateRequest request) {
        MachineResponse created = machineService.createMachine(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Machine created successfully", created));
    }

    @GetMapping("/machines")
    @Operation(summary = "Get all machines", description = "Retrieves all configured machine assets.")
    public ResponseEntity<ApiResponse<List<MachineResponse>>> getAllMachines() {
        List<MachineResponse> machines = machineService.getAllMachines();
        return ResponseEntity.ok(ApiResponse.success(machines));
    }

    @GetMapping("/machines/{id}")
    @Operation(summary = "Get machine by ID", description = "Retrieves machine asset details by database ID.")
    public ResponseEntity<ApiResponse<MachineResponse>> getMachineById(@PathVariable Long id) {
        MachineResponse machine = machineService.getMachineById(id);
        return ResponseEntity.ok(ApiResponse.success(machine));
    }

    @GetMapping("/lines/{lineId}/machines")
    @Operation(summary = "Get machines by line ID", description = "Retrieves all machine assets assigned to a production line ordered by station sequence.")
    public ResponseEntity<ApiResponse<List<MachineResponse>>> getMachinesByLine(@PathVariable Long lineId) {
        List<MachineResponse> machines = machineService.getMachinesByLine(lineId);
        return ResponseEntity.ok(ApiResponse.success(machines));
    }

    @PutMapping("/machines/{id}")
    @Operation(summary = "Update machine details", description = "Updates mutable properties of an existing machine asset.")
    public ResponseEntity<ApiResponse<MachineResponse>> updateMachine(
            @PathVariable Long id,
            @Valid @RequestBody MachineUpdateRequest request) {
        MachineResponse updated = machineService.updateMachine(id, request);
        return ResponseEntity.ok(ApiResponse.success("Machine updated successfully", updated));
    }

    @DeleteMapping("/machines/{id}")
    @Operation(summary = "Decommission machine", description = "Soft-deactivates machine asset (sets status to DECOMMISSIONED).")
    public ResponseEntity<Void> decommissionMachine(@PathVariable Long id) {
        machineService.decommissionMachine(id);
        return ResponseEntity.noContent().build();
    }
}
