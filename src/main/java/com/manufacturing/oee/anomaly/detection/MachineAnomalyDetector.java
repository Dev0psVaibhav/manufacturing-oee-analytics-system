package com.manufacturing.oee.anomaly.detection;

import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.machinestate.event.entity.StateEvent;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Detector for machine state anomalies, specifically ANOM-001 (Overlapping Operational States).
 */
@Component
public class MachineAnomalyDetector {

    public static final String CODE_OVERLAPPING_STATES = "EXC_ANOM_OVERLAPPING_MACHINE_STATES";

    /**
     * Scans chronological machine state events for temporal overlap collisions.
     */
    public List<AnomalyRecord> detectOverlappingStates(Machine machine, List<StateEvent> events) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (events == null || events.size() < 2) {
            return anomalies;
        }

        List<StateEvent> sortedEvents = events.stream()
                .filter(e -> e.getStartTime() != null)
                .sorted(Comparator.comparing(StateEvent::getStartTime))
                .toList();

        for (int i = 0; i < sortedEvents.size() - 1; i++) {
            StateEvent eventA = sortedEvents.get(i);
            StateEvent eventB = sortedEvents.get(i + 1);

            if (eventA.getEndTime() != null && eventB.getStartTime().isBefore(eventA.getEndTime())) {
                long overlapSeconds = Duration.between(eventB.getStartTime(), eventA.getEndTime()).toSeconds();
                if (overlapSeconds > 0) {
                    AnomalyRecord anomaly = new AnomalyRecord(
                            CODE_OVERLAPPING_STATES,
                            "StateEvent",
                            eventA.getId(),
                            eventB.getId(),
                            machine,
                            BigDecimal.valueOf(overlapSeconds),
                            "CRITICAL",
                            "Temporal overlap of " + overlapSeconds + "s detected between StateEvent "
                                    + eventA.getId() + " (" + eventA.getState() + ") and StateEvent "
                                    + eventB.getId() + " (" + eventB.getState() + ") on machine " + machine.getMachineCode()
                    );
                    anomalies.add(anomaly);
                }
            }
        }

        return anomalies;
    }
}
