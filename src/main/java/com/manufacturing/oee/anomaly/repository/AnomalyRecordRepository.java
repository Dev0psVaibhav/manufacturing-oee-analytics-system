package com.manufacturing.oee.anomaly.repository;

import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link AnomalyRecord} entity.
 */
@Repository
public interface AnomalyRecordRepository extends JpaRepository<AnomalyRecord, Long> {

    Optional<AnomalyRecord> findByAnomalyUuid(String anomalyUuid);

    boolean existsByAnomalyCodeAndEntityNameAndPrimaryRecordIdAndResolutionStatusNot(
            String anomalyCode,
            String entityName,
            Long primaryRecordId,
            String resolutionStatus
    );

    List<AnomalyRecord> findByMachineIdOrderByDetectionTimestampDesc(Long machineId);

    List<AnomalyRecord> findByResolutionStatus(String resolutionStatus);

    List<AnomalyRecord> findBySeverity(String severity);

    long countByResolutionStatus(String resolutionStatus);

    long countBySeverity(String severity);

    long countByAnomalyCode(String anomalyCode);

    @Query("""
        SELECT ar FROM AnomalyRecord ar
        WHERE (:machineId IS NULL OR ar.machine.id = :machineId)
          AND (:anomalyCode IS NULL OR ar.anomalyCode = :anomalyCode)
          AND (:severity IS NULL OR ar.severity = :severity)
          AND (:resolutionStatus IS NULL OR ar.resolutionStatus = :resolutionStatus)
          AND (:startDate IS NULL OR ar.detectionTimestamp >= :startDate)
          AND (:endDate IS NULL OR ar.detectionTimestamp <= :endDate)
        ORDER BY ar.detectionTimestamp DESC
    """)
    Page<AnomalyRecord> findWithFilters(
            @Param("machineId") Long machineId,
            @Param("anomalyCode") String anomalyCode,
            @Param("severity") String severity,
            @Param("resolutionStatus") String resolutionStatus,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );
}
