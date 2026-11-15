package com.manufacturing.oee.oee.repository;

import com.manufacturing.oee.oee.entity.OeeCalculationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link OeeCalculationRun} entity.
 */
@Repository
public interface OeeCalculationRunRepository extends JpaRepository<OeeCalculationRun, Long> {

    Optional<OeeCalculationRun> findByRunUuid(String runUuid);

    List<OeeCalculationRun> findByOrderByCalculationTimestampDesc();

    List<OeeCalculationRun> findByDateFromGreaterThanEqualAndDateToLessThanEqual(LocalDate dateFrom, LocalDate dateTo);
}
