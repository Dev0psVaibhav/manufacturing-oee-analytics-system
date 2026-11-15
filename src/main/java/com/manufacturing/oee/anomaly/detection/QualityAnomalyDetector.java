package com.manufacturing.oee.anomaly.detection;

import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.production.record.entity.ProductionRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Detector for quality anomalies, including ANOM-003 (Scrap Exceeds Total / Balance Violations).
 */
@Component
public class QualityAnomalyDetector {

    public static final String CODE_SCRAP_EXCEEDS_TOTAL = "EXC_ANOM_SCRAP_EXCEEDS_TOTAL";
    public static final String CODE_QUALITY_BALANCE_VIOLATION = "ERR_QLT_BALANCE_VIOLATION";

    /**
     * Inspects production records for quality violations (scrap exceeding total or balance violations).
     */
    public List<AnomalyRecord> detectQualityAnomalies(Machine machine, List<ProductionRecord> records) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return anomalies;
        }

        for (ProductionRecord record : records) {
            int total = record.getTotalQuantity() != null ? record.getTotalQuantity() : 0;
            int good = record.getGoodQuantity() != null ? record.getGoodQuantity() : 0;
            int scrap = record.getRejectedQuantity() != null ? record.getRejectedQuantity() : 0;

            // Anomaly: scrap > total
            if (scrap > total) {
                int excess = scrap - total;
                AnomalyRecord anomaly = new AnomalyRecord(
                        CODE_SCRAP_EXCEEDS_TOTAL,
                        "ProductionRecord",
                        record.getId(),
                        null,
                        machine,
                        BigDecimal.valueOf(excess),
                        "CRITICAL",
                        "Scrap quantity (" + scrap + ") exceeds total quantity produced (" + total
                                + ") on machine " + machine.getMachineCode() + " in record " + record.getRecordCode()
                );
                anomalies.add(anomaly);
            }
            // Balance violation: good + scrap != total
            else if (total > 0 && (good + scrap != total)) {
                int mismatch = Math.abs(total - (good + scrap));
                AnomalyRecord anomaly = new AnomalyRecord(
                        CODE_QUALITY_BALANCE_VIOLATION,
                        "ProductionRecord",
                        record.getId(),
                        null,
                        machine,
                        BigDecimal.valueOf(mismatch),
                        "CRITICAL",
                        "Quality balance violation on machine " + machine.getMachineCode()
                                + ": Total (" + total + ") != Good (" + good + ") + Rejected (" + scrap + ")"
                );
                anomalies.add(anomaly);
            }
        }

        return anomalies;
    }
}
