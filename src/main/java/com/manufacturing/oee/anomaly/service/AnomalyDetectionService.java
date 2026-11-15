package com.manufacturing.oee.anomaly.service;

import com.manufacturing.oee.anomaly.dto.AnomalyDetectionRequest;
import com.manufacturing.oee.anomaly.dto.AnomalyResponse;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for running rule-based anomaly detection algorithms across equipment and transactions.
 */
public interface AnomalyDetectionService {

    /**
     * Executes anomaly detection based on request parameters (scope, target, time bounds).
     */
    List<AnomalyResponse> runDetection(AnomalyDetectionRequest request);

    /**
     * Executes detection for a specific machine over a timestamp window.
     */
    List<AnomalyResponse> detectForMachine(Long machineId, Instant windowStart, Instant windowEnd, boolean persist);

    /**
     * Executes detection across all machines in a production line over a timestamp window.
     */
    List<AnomalyResponse> detectForLine(Long lineId, Instant windowStart, Instant windowEnd, boolean persist);

    /**
     * Executes detection for an entire scheduled shift instance.
     */
    List<AnomalyResponse> detectForShift(Long shiftInstanceId, boolean persist);
}
