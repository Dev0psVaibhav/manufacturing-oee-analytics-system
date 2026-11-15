package com.manufacturing.oee.system;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.system.dto.SystemStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * REST controller exposing lightweight system status and health check endpoints.
 * Provides a verified smoke check for the REST API foundation without invoking manufacturing logic.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/system")
@Tag(name = "System Foundation", description = "System operational status and runtime health diagnostics")
public class SystemStatusController {

    private final Environment environment;
    private final Clock clock;

    @Value("${spring.application.name:manufacturing-oee-analytics}")
    private String applicationName;

    public SystemStatusController(Environment environment, Clock clock) {
        this.environment = environment;
        this.clock = clock;
    }

    @GetMapping("/status")
    @Operation(summary = "Get system foundation status", description = "Returns the operational status, version, and active profiles of the application.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "System foundation is healthy and running")
    })
    public ResponseEntity<ApiResponse<SystemStatusResponse>> getStatus() {
        String[] profiles = environment.getActiveProfiles().length == 0
                ? environment.getDefaultProfiles()
                : environment.getActiveProfiles();

        SystemStatusResponse payload = new SystemStatusResponse(
                applicationName,
                "UP",
                "0.0.1-SNAPSHOT",
                Instant.now(clock),
                Arrays.asList(profiles),
                "Manufacturing OEE Analytics System - Phase 3 Foundation Ready"
        );

        return ResponseEntity.ok(ApiResponse.success("System foundation operational", payload));
    }
}
