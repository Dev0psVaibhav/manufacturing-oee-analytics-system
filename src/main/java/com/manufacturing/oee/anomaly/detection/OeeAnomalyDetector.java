package com.manufacturing.oee.anomaly.detection;

import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.entity.MachineProductConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Detector for OEE and engineering standard calibration anomalies,
 * specifically ANOM-004 (Wrong Ideal Cycle Time).
 */
@Component
public class OeeAnomalyDetector {

    public static final String CODE_WRONG_IDEAL_CYCLE_TIME = "EXC_ANOM_WRONG_IDEAL_CYCLE_TIME";
    public static final String CODE_OEE_STATUS_INVALID = "ANOM_OEE_STATUS_INVALID";

    /**
     * Detects discrepancy between applied cycle time and authoritative active standard.
     */
    public List<AnomalyRecord> detectCycleTimeMismatch(
            Machine machine,
            MachineProductConfig config,
            BigDecimal appliedCycleTime
    ) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (config == null || appliedCycleTime == null || config.getIdealCycleTimeSeconds() == null) {
            return anomalies;
        }

        BigDecimal configured = config.getIdealCycleTimeSeconds();
        BigDecimal diff = appliedCycleTime.subtract(configured).abs();

        if (diff.compareTo(new BigDecimal("0.001")) > 0) {
            AnomalyRecord anomaly = new AnomalyRecord(
                    CODE_WRONG_IDEAL_CYCLE_TIME,
                    "MachineProductConfig",
                    config.getId(),
                    null,
                    machine,
                    diff,
                    "ERROR",
                    "Cycle time mismatch for machine " + machine.getMachineCode() + " and product "
                            + config.getProduct().getProductCode() + ": applied " + appliedCycleTime + "s vs configured " + configured + "s"
            );
            anomalies.add(anomaly);
        }

        return anomalies;
    }

    /**
     * Flags when an OEE aggregate result is marked INVALID due to upstream data corruption.
     */
    public List<AnomalyRecord> detectInvalidOeeResult(Machine machine, Long oeeAggregateId, String oeeStatus, String reason) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if ("INVALID".equalsIgnoreCase(oeeStatus)) {
            AnomalyRecord anomaly = new AnomalyRecord(
                    CODE_OEE_STATUS_INVALID,
                    "OeeAggregate",
                    oeeAggregateId != null ? oeeAggregateId : 0L,
                    null,
                    machine,
                    BigDecimal.ZERO,
                    "ERROR",
                    "OEE calculation resulted in INVALID status on machine " + machine.getMachineCode() + ": " + reason
            );
            anomalies.add(anomaly);
        }
        return anomalies;
    }
}
