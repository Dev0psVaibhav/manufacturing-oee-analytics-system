package com.manufacturing.oee.oee.calculation;

import com.manufacturing.oee.oee.calculation.model.AvailabilityResult;
import com.manufacturing.oee.oee.calculation.model.OeeCalculationResult;
import com.manufacturing.oee.oee.calculation.model.PerformanceResult;
import com.manufacturing.oee.oee.calculation.model.QualityResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite OEE Calculation engine.
 * Synthesizes Availability, Performance, and Quality into overall OEE
 * and derives ISO 22400-2 Fully Productive Time (FPT).
 */
@Component
public class OeeCalculator {

    /**
     * Synthesizes component calculations into a consolidated OeeCalculationResult.
     */
    public OeeCalculationResult synthesize(
            Long plantId,
            Long lineId,
            String lineCode,
            Long machineId,
            String machineCode,
            Long shiftInstanceId,
            LocalDate operationalDate,
            Instant windowStart,
            Instant windowEnd,
            AvailabilityResult availability,
            PerformanceResult performance,
            QualityResult quality
    ) {
        List<String> warnings = new ArrayList<>();

        if (availability.notes() != null && !availability.notes().contains("successfully")) {
            warnings.add("Availability: " + availability.notes());
        }
        if (performance.notes() != null && !performance.notes().contains("successfully")) {
            warnings.add("Performance: " + performance.notes());
        }
        if (quality.notes() != null && !quality.notes().contains("successfully")) {
            warnings.add("Quality: " + quality.notes());
        }

        // Determine Composite Status
        String compositeStatus;
        if ("EXCLUDED".equals(availability.status())) {
            compositeStatus = "EXCLUDED";
        } else if ("INVALID".equals(availability.status())
                || "INVALID".equals(performance.status())
                || "INVALID".equals(quality.status())) {
            compositeStatus = "INVALID";
        } else if ("VALID_WITH_WARNINGS".equals(availability.status())
                || "VALID_WITH_WARNINGS".equals(performance.status())
                || "VALID_WITH_WARNINGS".equals(quality.status())) {
            compositeStatus = "VALID_WITH_WARNINGS";
        } else {
            compositeStatus = "VALID";
        }

        BigDecimal oeeRatio;
        BigDecimal oeePercentage;
        int plannedProductionSeconds = availability.plannedProductionSeconds();
        int operatingSeconds = availability.operatingSeconds();
        int netOperatingSeconds = performance.netOperatingSeconds().setScale(0, RoundingMode.HALF_UP).intValue();
        int totalParts = quality.totalPartsProduced();
        int goodParts = quality.goodPartsProduced();

        if ("EXCLUDED".equals(compositeStatus) || "INVALID".equals(compositeStatus)) {
            oeeRatio = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
            oeePercentage = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        } else {
            oeeRatio = availability.availabilityRatio()
                    .multiply(performance.performanceRatio())
                    .multiply(quality.qualityRatio())
                    .setScale(6, RoundingMode.HALF_UP);

            oeePercentage = oeeRatio
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(4, RoundingMode.HALF_UP);
        }

        // Fully Productive Time (seconds of standard production time dedicated solely to conforming parts)
        // FPT = PPT * OEE ratio
        int fullyProductiveSeconds = plannedProductionSeconds > 0
                ? oeeRatio.multiply(BigDecimal.valueOf(plannedProductionSeconds)).setScale(0, RoundingMode.HALF_UP).intValue()
                : 0;

        return new OeeCalculationResult(
                plantId,
                lineId,
                lineCode,
                machineId,
                machineCode,
                shiftInstanceId,
                operationalDate,
                windowStart,
                windowEnd,
                availability,
                performance,
                quality,
                plannedProductionSeconds,
                operatingSeconds,
                netOperatingSeconds,
                fullyProductiveSeconds,
                totalParts,
                goodParts,
                availability.availabilityRatio(),
                performance.performanceRatio(),
                quality.qualityRatio(),
                oeeRatio,
                oeePercentage,
                compositeStatus,
                warnings
        );
    }
}
