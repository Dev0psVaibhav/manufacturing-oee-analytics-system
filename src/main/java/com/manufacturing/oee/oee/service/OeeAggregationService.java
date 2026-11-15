package com.manufacturing.oee.oee.service;

import com.manufacturing.oee.oee.dto.OeeCalculationRequest;
import com.manufacturing.oee.oee.dto.OeeCalculationRunResponse;
import com.manufacturing.oee.oee.entity.OeeAggregate;

import java.util.List;

/**
 * Service contract for persistent OEE calculation batch execution, snapshot aggregation,
 * and historical snapshot queries.
 */
public interface OeeAggregationService {

    /**
     * Executes an OEE calculation batch across requested scope and persists aggregates.
     */
    OeeCalculationRunResponse executeAndPersistRun(OeeCalculationRequest request);

    /**
     * Retrieves recent calculation execution runs.
     */
    List<OeeCalculationRunResponse> getRecentRuns();

    /**
     * Retrieves a calculation run by ID.
     */
    OeeCalculationRunResponse getRunById(Long runId);

    /**
     * Retrieves persisted OEE aggregate snapshots for a calculation run.
     */
    List<com.manufacturing.oee.oee.dto.OeeAggregateResponse> getAggregatesForRun(Long runId);
}
