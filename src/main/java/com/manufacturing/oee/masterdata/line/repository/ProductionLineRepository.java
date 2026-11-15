package com.manufacturing.oee.masterdata.line.repository;

import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ProductionLine} entity.
 */
@Repository
public interface ProductionLineRepository extends JpaRepository<ProductionLine, Long> {

    List<ProductionLine> findByPlantId(Long plantId);

    Optional<ProductionLine> findByPlantIdAndLineCode(Long plantId, String lineCode);

    boolean existsByPlantIdAndLineCode(Long plantId, String lineCode);

    boolean existsByPlantIdAndSequenceOrder(Long plantId, Integer sequenceOrder);

    List<ProductionLine> findByIsActiveTrue();
}
