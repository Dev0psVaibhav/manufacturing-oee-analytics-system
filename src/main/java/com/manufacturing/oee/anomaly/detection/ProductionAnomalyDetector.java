package com.manufacturing.oee.anomaly.detection;

import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.production.record.entity.ProductionRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Detector for production telemetry anomalies, including ANOM-002 (Negative/Rollback Counts)
 * and phantom production during zero operating time.
 */
@Component
public class ProductionAnomalyDetector {

    public static final String CODE_UNCORRECTED_COUNT_DECREASE = "EXC_ANOM_UNCORRECTED_COUNT_DECREASE";
    public static final String CODE_PHANTOM_PRODUCTION = "ANOM_PHANTOM_PRODUCTION_ZERO_RUNTIME";

    /**
     * Scans production records for negative quantities or counter rollbacks.
     */
    public List<AnomalyRecord> detectProductionAnomalies(Machine machine, List<ProductionRecord> records) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return anomalies;
        }

        for (ProductionRecord record : records) {
            // Negative production check
            if (record.getTotalQuantity() != null && record.getTotalQuantity() < 0) {
                AnomalyRecord anomaly = new AnomalyRecord(
                        CODE_UNCORRECTED_COUNT_DECREASE,
                        "ProductionRecord",
                        record.getId(),
                        null,
                        machine,
                        BigDecimal.valueOf(Math.abs(record.getTotalQuantity())),
                        "CRITICAL",
                        "Negative production quantity (" + record.getTotalQuantity()
                                + ") detected on machine " + machine.getMachineCode() + " in record " + record.getRecordCode()
                );
                anomalies.add(anomaly);
            }
        }

        return anomalies;
    }

    /**
     * Checks for phantom production (units reported when operating seconds is zero).
     */
    public List<AnomalyRecord> detectPhantomProduction(Machine machine, int operatingSeconds, int totalPartsCount, Long primaryRecordId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (operatingSeconds <= 0 && totalPartsCount > 0) {
            AnomalyRecord anomaly = new AnomalyRecord(
                    CODE_PHANTOM_PRODUCTION,
                    "ProductionRecord",
                    primaryRecordId != null ? primaryRecordId : 0L,
                    null,
                    machine,
                    BigDecimal.valueOf(totalPartsCount),
                    "CRITICAL",
                    "Phantom production: " + totalPartsCount + " units produced on machine "
                            + machine.getMachineCode() + " while operating time is 0 seconds"
            );
            anomalies.add(anomaly);
        }
        return anomalies;
    }
}
