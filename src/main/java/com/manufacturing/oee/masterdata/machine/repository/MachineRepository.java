package com.manufacturing.oee.masterdata.machine.repository;

import com.manufacturing.oee.masterdata.machine.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Machine} entity.
 */
@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    Optional<Machine> findByMachineCode(String machineCode);

    boolean existsByMachineCode(String machineCode);

    List<Machine> findByLineId(Long lineId);

    List<Machine> findByLineIdOrderByStationSequenceAsc(Long lineId);

    boolean existsByLineIdAndStationSequence(Long lineId, Integer stationSequence);

    List<Machine> findByStatus(String status);
}
