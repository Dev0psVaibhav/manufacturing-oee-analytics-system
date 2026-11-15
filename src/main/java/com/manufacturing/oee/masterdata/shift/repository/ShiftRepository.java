package com.manufacturing.oee.masterdata.shift.repository;

import com.manufacturing.oee.masterdata.shift.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Shift} entity.
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findByShiftCode(String shiftCode);

    boolean existsByShiftCode(String shiftCode);
}
