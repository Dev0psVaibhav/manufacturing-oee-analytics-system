package com.manufacturing.oee.anomaly.detection;

import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Detector for downtime anomalies, including ANOM-005 (Downtime Overlapping Excluded Planned Break)
 * and unclosed excessive downtime incidents.
 */
@Component
public class DowntimeAnomalyDetector {

    public static final String CODE_DOWNTIME_OVERLAPS_BREAK = "EXC_ANOM_DOWNTIME_OVERLAPS_PLANNED_BREAK";
    public static final String CODE_EXCESSIVE_UNCLOSED_DOWNTIME = "ANOM_EXCESSIVE_UNCLOSED_DOWNTIME";
    public static final long MAX_DOWNTIME_SECONDS_THRESHOLD = 43200L; // 12 hours

    /**
     * Checks if downtime records overlap with a scheduled planned break interval.
     */
    public List<AnomalyRecord> detectBreakOverlaps(
            Machine machine,
            List<DowntimeRecord> downtimeRecords,
            Instant breakStart,
            Instant breakEnd
    ) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (downtimeRecords == null || breakStart == null || breakEnd == null || breakStart.isAfter(breakEnd)) {
            return anomalies;
        }

        for (DowntimeRecord dr : downtimeRecords) {
            Instant downStart = dr.getStartTime();
            Instant downEnd = dr.getEndTime() != null ? dr.getEndTime() : Instant.now();

            Instant overlapStart = downStart.isAfter(breakStart) ? downStart : breakStart;
            Instant overlapEnd = downEnd.isBefore(breakEnd) ? downEnd : breakEnd;

            if (overlapStart.isBefore(overlapEnd)) {
                long overlapSeconds = Duration.between(overlapStart, overlapEnd).toSeconds();
                if (overlapSeconds > 0) {
                    AnomalyRecord anomaly = new AnomalyRecord(
                            CODE_DOWNTIME_OVERLAPS_BREAK,
                            "DowntimeRecord",
                            dr.getId(),
                            null,
                            machine,
                            BigDecimal.valueOf(overlapSeconds),
                            "WARNING",
                            "Downtime record " + dr.getDowntimeCode() + " overlaps scheduled break by "
                                    + overlapSeconds + "s; break overlap must be deducted per BR-BRK-003"
                    );
                    anomalies.add(anomaly);
                }
            }

            // Also check for unclosed excessive downtime (> 12 hours)
            if (dr.getEndTime() == null) {
                long openSeconds = Duration.between(dr.getStartTime(), Instant.now()).toSeconds();
                if (openSeconds > MAX_DOWNTIME_SECONDS_THRESHOLD) {
                    AnomalyRecord excessiveAnomaly = new AnomalyRecord(
                            CODE_EXCESSIVE_UNCLOSED_DOWNTIME,
                            "DowntimeRecord",
                            dr.getId(),
                            null,
                            machine,
                            BigDecimal.valueOf(openSeconds),
                            "WARNING",
                            "Unclosed downtime " + dr.getDowntimeCode() + " has been open for "
                                    + (openSeconds / 3600) + " hours, exceeding standard shift limits"
                    );
                    anomalies.add(excessiveAnomaly);
                }
            }
        }

        return anomalies;
    }
}
