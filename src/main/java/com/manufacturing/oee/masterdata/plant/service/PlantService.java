package com.manufacturing.oee.masterdata.plant.service;

import com.manufacturing.oee.masterdata.plant.dto.PlantCreateRequest;
import com.manufacturing.oee.masterdata.plant.dto.PlantResponse;
import com.manufacturing.oee.masterdata.plant.dto.PlantUpdateRequest;

import java.util.List;

/**
 * Service interface for Plant master data operations.
 */
public interface PlantService {

    PlantResponse createPlant(PlantCreateRequest request);

    PlantResponse getPlantById(Long id);

    PlantResponse getPlantByCode(String plantCode);

    List<PlantResponse> getAllPlants();

    PlantResponse updatePlant(Long id, PlantUpdateRequest request);

    void deactivatePlant(Long id);
}
