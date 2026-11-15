package com.manufacturing.oee.masterdata.line.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.line.dto.LineCreateRequest;
import com.manufacturing.oee.masterdata.line.dto.LineResponse;
import com.manufacturing.oee.masterdata.line.dto.LineUpdateRequest;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.plant.repository.PlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link LineService} enforcing business rules for ProductionLine entities.
 */
@Service
@Transactional(readOnly = true)
public class LineServiceImpl implements LineService {

    private static final Logger log = LoggerFactory.getLogger(LineServiceImpl.class);

    private final ProductionLineRepository lineRepository;
    private final PlantRepository plantRepository;

    public LineServiceImpl(ProductionLineRepository lineRepository, PlantRepository plantRepository) {
        this.lineRepository = lineRepository;
        this.plantRepository = plantRepository;
    }

    @Override
    @Transactional
    public LineResponse createLine(LineCreateRequest request) {
        log.info("Creating production line with code: {} for plant ID: {}", request.lineCode(), request.plantId());

        Plant plant = plantRepository.findById(request.plantId())
                .orElseThrow(() -> new ResourceNotFoundException("Plant", String.valueOf(request.plantId())));

        if (lineRepository.existsByPlantIdAndLineCode(request.plantId(), request.lineCode())) {
            throw new BusinessException("DUPLICATE_LINE_CODE",
                    "Line with code '" + request.lineCode() + "' already exists in plant ID " + request.plantId());
        }

        ProductionLine line = new ProductionLine(
                plant,
                request.lineCode(),
                request.lineName(),
                request.sequenceOrder(),
                request.lineType(),
                request.isActive()
        );

        ProductionLine saved = lineRepository.save(line);
        log.info("Production line created successfully with ID: {}", saved.getId());
        return LineResponse.fromEntity(saved);
    }

    @Override
    public LineResponse getLineById(Long id) {
        ProductionLine line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(id)));
        return LineResponse.fromEntity(line);
    }

    @Override
    public List<LineResponse> getAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::fromEntity)
                .toList();
    }

    @Override
    public List<LineResponse> getLinesByPlantId(Long plantId) {
        if (!plantRepository.existsById(plantId)) {
            throw new ResourceNotFoundException("Plant", String.valueOf(plantId));
        }
        return lineRepository.findByPlantId(plantId).stream()
                .map(LineResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public LineResponse updateLine(Long id, LineUpdateRequest request) {
        log.info("Updating production line ID: {}", id);
        ProductionLine line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(id)));

        line.setLineName(request.lineName());
        line.setSequenceOrder(request.sequenceOrder());
        if (request.lineType() != null) {
            line.setLineType(request.lineType());
        }
        if (request.isActive() != null) {
            line.setIsActive(request.isActive());
        }

        ProductionLine updated = lineRepository.save(line);
        return LineResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deactivateLine(Long id) {
        log.info("Deactivating production line ID: {}", id);
        ProductionLine line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(id)));

        // Soft-deactivation to preserve referential integrity
        line.setIsActive(false);
        lineRepository.save(line);
        log.info("Production line ID: {} deactivated successfully", id);
    }
}
