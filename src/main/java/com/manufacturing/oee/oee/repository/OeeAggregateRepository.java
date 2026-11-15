package com.manufacturing.oee.oee.repository;

import com.manufacturing.oee.oee.entity.OeeAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link OeeAggregate} entity.
 */
@Repository
public interface OeeAggregateRepository extends JpaRepository<OeeAggregate, Long> {

    List<OeeAggregate> findByCalculationRunId(Long calculationRunId);

    List<OeeAggregate> findByMachineIdAndOperationalDateBetween(Long machineId, LocalDate startDate, LocalDate endDate);

    List<OeeAggregate> findByLineIdAndOperationalDateBetween(Long lineId, LocalDate startDate, LocalDate endDate);

    List<OeeAggregate> findByPlantIdAndOperationalDateBetween(Long plantId, LocalDate startDate, LocalDate endDate);

    Optional<OeeAggregate> findByMachineIdAndShiftInstanceId(Long machineId, Long shiftInstanceId);
}
