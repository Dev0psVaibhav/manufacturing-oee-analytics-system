package com.manufacturing.oee.masterdata.plant.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.plant.dto.PlantCreateRequest;
import com.manufacturing.oee.masterdata.plant.dto.PlantResponse;
import com.manufacturing.oee.masterdata.plant.dto.PlantUpdateRequest;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.plant.repository.PlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link PlantService} enforcing business rules for Plant entities.
 */
@Service
@Transactional(readOnly = true)
public class PlantServiceImpl implements PlantService {

    private static final Logger log = LoggerFactory.getLogger(PlantServiceImpl.class);

    private final PlantRepository plantRepository;

    public PlantServiceImpl(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    @Transactional
    public PlantResponse createPlant(PlantCreateRequest request) {
        log.info("Creating plant with code: {}", request.plantCode());
        if (plantRepository.existsByPlantCode(request.plantCode())) {
            throw new BusinessException("DUPLICATE_PLANT_CODE", "Plant with code '" + request.plantCode() + "' already exists");
        }

        Plant plant = new Plant(
                request.plantCode(),
                request.plantName(),
                request.timeZone(),
                request.countryCode(),
                request.status() != null ? request.status() : "ACTIVE"
        );

        Plant saved = plantRepository.save(plant);
        log.info("Plant created successfully with ID: {}", saved.getId());
        return PlantResponse.fromEntity(saved);
    }

    @Override
    public PlantResponse getPlantById(Long id) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", String.valueOf(id)));
        return PlantResponse.fromEntity(plant);
    }

    @Override
    public PlantResponse getPlantByCode(String plantCode) {
        Plant plant = plantRepository.findByPlantCode(plantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", plantCode));
        return PlantResponse.fromEntity(plant);
    }

    @Override
    public List<PlantResponse> getAllPlants() {
        return plantRepository.findAll().stream()
                .map(PlantResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public PlantResponse updatePlant(Long id, PlantUpdateRequest request) {
        log.info("Updating plant ID: {}", id);
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", String.valueOf(id)));

        plant.setPlantName(request.plantName());
        plant.setTimeZone(request.timeZone());
        plant.setCountryCode(request.countryCode());
        if (request.status() != null) {
            plant.setStatus(request.status());
        }

        Plant updated = plantRepository.save(plant);
        return PlantResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deactivatePlant(Long id) {
        log.info("Deactivating plant ID: {}", id);
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", String.valueOf(id)));

        // Soft-deactivation to preserve referential integrity per BR-ARC-003
        plant.setStatus("INACTIVE");
        plantRepository.save(plant);
        log.info("Plant ID: {} deactivated successfully", id);
    }
}
