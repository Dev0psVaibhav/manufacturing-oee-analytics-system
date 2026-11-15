package com.manufacturing.oee.machinestate.downtime.repository;

import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link DowntimeRecord} entity.
 */
@Repository
public interface DowntimeRecordRepository extends JpaRepository<DowntimeRecord, Long> {

    Optional<DowntimeRecord> findByDowntimeCode(String downtimeCode);

    boolean existsByDowntimeCode(String downtimeCode);

    List<DowntimeRecord> findByMachineId(Long machineId);

    List<DowntimeRecord> findByLineId(Long lineId);

    List<DowntimeRecord> findByStatus(String status);

    List<DowntimeRecord> findByMachineIdAndStatus(Long machineId, String status);

    List<DowntimeRecord> findByMachineIdAndShiftInstanceId(Long machineId, Long shiftInstanceId);

    List<DowntimeRecord> findByLineIdAndShiftInstanceId(Long lineId, Long shiftInstanceId);

    @Query("""
        SELECT dr FROM DowntimeRecord dr
        WHERE dr.machine.id = :machineId
          AND dr.startTime < :windowEnd
          AND (dr.endTime IS NULL OR dr.endTime > :windowStart)
        ORDER BY dr.startTime ASC
    """)
    List<DowntimeRecord> findOverlappingDowntimes(
            @Param("machineId") Long machineId,
            @Param("windowStart") Instant windowStart,
            @Param("windowEnd") Instant windowEnd
    );

    @Query("""
        SELECT dr FROM DowntimeRecord dr
        WHERE (:machineId IS NULL OR dr.machine.id = :machineId)
          AND (:lineId IS NULL OR dr.line.id = :lineId)
          AND (:status IS NULL OR dr.status = :status)
          AND (:startDate IS NULL OR dr.startTime >= :startDate)
          AND (:endDate IS NULL OR dr.startTime <= :endDate)
        ORDER BY dr.startTime DESC
    """)
    List<DowntimeRecord> findDowntimeWithFilters(
            @Param("machineId") Long machineId,
            @Param("lineId") Long lineId,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}
