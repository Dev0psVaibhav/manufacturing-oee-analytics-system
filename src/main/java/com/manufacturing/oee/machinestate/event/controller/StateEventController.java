package com.manufacturing.oee.machinestate.event.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.machinestate.event.dto.StateEventCreateRequest;
import com.manufacturing.oee.machinestate.event.dto.StateEventResponse;
import com.manufacturing.oee.machinestate.event.service.StateEventService;
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
 * REST controller for Machine Operational State Events and telemetry history.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX)
@Tag(name = "Machine State Events", description = "Endpoints for logging machine state transitions and inspecting state event history")
public class StateEventController {

    private final StateEventService eventService;

    public StateEventController(StateEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/machine-state-events")
    @Operation(summary = "Record machine state transition", description = "Appends a new discrete machine state event (RUNNING, UNPLANNED_DOWNTIME, etc.) and auto-closes prior state.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "State event recorded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload, invalid timestamp window, or missing downtime reason"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate event code")
    })
    public ResponseEntity<ApiResponse<StateEventResponse>> recordEvent(@Valid @RequestBody StateEventCreateRequest request) {
        StateEventResponse created = eventService.recordStateTransition(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Machine state event recorded successfully", created));
    }

    @GetMapping("/machine-state-events")
    @Operation(summary = "Search machine state events", description = "Retrieves state events filtered by machine ID, operating state, or timestamp interval.")
    public ResponseEntity<ApiResponse<List<StateEventResponse>>> searchEvents(
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<StateEventResponse> events = eventService.searchEvents(machineId, state, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/machine-state-events/{id}")
    @Operation(summary = "Get state event by ID", description = "Retrieves state event details by surrogate database ID.")
    public ResponseEntity<ApiResponse<StateEventResponse>> getEventById(@PathVariable Long id) {
        StateEventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @GetMapping("/machines/{machineId}/state-history")
    @Operation(summary = "Get machine state history", description = "Retrieves chronological state transition event stream for a specific machine asset.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "State history retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Machine not found")
    })
    public ResponseEntity<ApiResponse<List<StateEventResponse>>> getMachineStateHistory(@PathVariable Long machineId) {
        List<StateEventResponse> history = eventService.getMachineStateHistory(machineId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
