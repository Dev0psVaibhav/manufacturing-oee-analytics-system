package com.manufacturing.oee.masterdata.machine.repository;

import com.manufacturing.oee.masterdata.machine.entity.MachineProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link MachineProductConfig} entity.
 */
@Repository
public interface MachineProductConfigRepository extends JpaRepository<MachineProductConfig, Long> {

    List<MachineProductConfig> findByMachineId(Long machineId);

    List<MachineProductConfig> findByProductId(Long productId);

    @Query("""
        SELECT mpc FROM MachineProductConfig mpc
        WHERE mpc.machine.id = :machineId
          AND mpc.product.id = :productId
          AND mpc.isActive = true
          AND mpc.effectiveFrom <= :timestamp
          AND (mpc.effectiveTo IS NULL OR mpc.effectiveTo > :timestamp)
        ORDER BY mpc.effectiveFrom DESC
    """)
    Optional<MachineProductConfig> findEffectiveConfig(
            @Param("machineId") Long machineId,
            @Param("productId") Long productId,
            @Param("timestamp") Instant timestamp
    );
}
