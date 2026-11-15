package com.manufacturing.oee.anomaly.repository;

import com.manufacturing.oee.anomaly.entity.AnomalyGroundTruth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link AnomalyGroundTruth} entity.
 */
@Repository
public interface AnomalyGroundTruthRepository extends JpaRepository<AnomalyGroundTruth, Long> {

    Optional<AnomalyGroundTruth> findByInjectionUuid(String injectionUuid);

    List<AnomalyGroundTruth> findByAnomalyCode(String anomalyCode);

    List<AnomalyGroundTruth> findByEntityNameAndTargetRecordId(String entityName, Long targetRecordId);
}
