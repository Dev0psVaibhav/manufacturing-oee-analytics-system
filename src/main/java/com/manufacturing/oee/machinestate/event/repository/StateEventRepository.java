package com.manufacturing.oee.machinestate.event.repository;

import com.manufacturing.oee.machinestate.event.entity.StateEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link StateEvent} entity.
 */
@Repository
public interface StateEventRepository extends JpaRepository<StateEvent, Long> {

    Optional<StateEvent> findByEventCode(String eventCode);

    boolean existsByEventCode(String eventCode);

    List<StateEvent> findByMachineIdOrderByStartTimeDesc(Long machineId);

    List<StateEvent> findByMachineIdAndStartTimeBetweenOrderByStartTimeAsc(Long machineId, Instant start, Instant end);

    Optional<StateEvent> findTopByMachineIdOrderByStartTimeDesc(Long machineId);

    List<StateEvent> findByState(String state);

    @Query("""
        SELECT se FROM StateEvent se
        WHERE (:machineId IS NULL OR se.machine.id = :machineId)
          AND (:state IS NULL OR se.state = :state)
          AND (:startDate IS NULL OR se.startTime >= :startDate)
          AND (:endDate IS NULL OR se.startTime <= :endDate)
        ORDER BY se.startTime DESC
    """)
    List<StateEvent> findEventsWithFilters(
            @Param("machineId") Long machineId,
            @Param("state") String state,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}
