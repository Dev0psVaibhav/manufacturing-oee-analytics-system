package com.manufacturing.oee.production.record.repository;

import com.manufacturing.oee.production.record.entity.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ProductionRecord} entity.
 */
@Repository
public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {

    Optional<ProductionRecord> findByRecordCode(String recordCode);

    boolean existsByRecordCode(String recordCode);

    List<ProductionRecord> findByProductionOrderId(Long productionOrderId);

    List<ProductionRecord> findByPlantId(Long plantId);

    List<ProductionRecord> findByLineId(Long lineId);

    List<ProductionRecord> findByMachineId(Long machineId);

    List<ProductionRecord> findByProductId(Long productId);

    List<ProductionRecord> findByShiftInstanceId(Long shiftInstanceId);

    List<ProductionRecord> findByMachineIdAndShiftInstanceId(Long machineId, Long shiftInstanceId);

    List<ProductionRecord> findByLineIdAndShiftInstanceId(Long lineId, Long shiftInstanceId);

    List<ProductionRecord> findByMachineIdAndProductionTimestampBetween(Long machineId, Instant start, Instant end);

    @Query("""
        SELECT pr FROM ProductionRecord pr
        WHERE (:orderId IS NULL OR pr.productionOrder.id = :orderId)
          AND (:plantId IS NULL OR pr.plant.id = :plantId)
          AND (:lineId IS NULL OR pr.line.id = :lineId)
          AND (:machineId IS NULL OR pr.machine.id = :machineId)
          AND (:productId IS NULL OR pr.product.id = :productId)
          AND (:shiftInstanceId IS NULL OR pr.shiftInstance.id = :shiftInstanceId)
          AND (:startDate IS NULL OR pr.productionTimestamp >= :startDate)
          AND (:endDate IS NULL OR pr.productionTimestamp <= :endDate)
        ORDER BY pr.productionTimestamp DESC
    """)
    List<ProductionRecord> findRecordsWithFilters(
            @Param("orderId") Long orderId,
            @Param("plantId") Long plantId,
            @Param("lineId") Long lineId,
            @Param("machineId") Long machineId,
            @Param("productId") Long productId,
            @Param("shiftInstanceId") Long shiftInstanceId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}
