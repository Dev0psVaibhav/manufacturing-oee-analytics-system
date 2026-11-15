package com.manufacturing.oee.masterdata.shift.repository;

import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link ShiftInstance} entities.
 */
@Repository
public interface ShiftInstanceRepository extends JpaRepository<ShiftInstance, Long> {

    Optional<ShiftInstance> findByShiftInstanceCode(String shiftInstanceCode);

    boolean existsByShiftInstanceCode(String shiftInstanceCode);

    List<ShiftInstance> findByShiftId(Long shiftId);

    List<ShiftInstance> findByOperationalDate(LocalDate operationalDate);

    List<ShiftInstance> findByOperationalDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<ShiftInstance> findByShiftIdAndOperationalDate(Long shiftId, LocalDate operationalDate);

    boolean existsByShiftIdAndOperationalDate(Long shiftId, LocalDate operationalDate);

    boolean existsByOperationalDateAndShiftId(LocalDate operationalDate, Long shiftId);
}
