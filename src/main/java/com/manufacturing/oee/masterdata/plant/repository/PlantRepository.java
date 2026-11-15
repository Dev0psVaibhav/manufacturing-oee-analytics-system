package com.manufacturing.oee.masterdata.plant.repository;

import com.manufacturing.oee.masterdata.plant.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Plant} entity.
 */
@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {

    Optional<Plant> findByPlantCode(String plantCode);

    boolean existsByPlantCode(String plantCode);

    List<Plant> findByStatus(String status);
}
