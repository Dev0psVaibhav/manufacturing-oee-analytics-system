package com.manufacturing.oee.masterdata.machine.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.machine.dto.MachineCreateRequest;
import com.manufacturing.oee.masterdata.machine.dto.MachineResponse;
import com.manufacturing.oee.masterdata.machine.dto.MachineUpdateRequest;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link MachineService} enforcing machine asset business rules.
 */
@Service
@Transactional(readOnly = true)
public class MachineServiceImpl implements MachineService {

    private static final Logger log = LoggerFactory.getLogger(MachineServiceImpl.class);

    private final MachineRepository machineRepository;
    private final ProductionLineRepository lineRepository;

    public MachineServiceImpl(MachineRepository machineRepository, ProductionLineRepository lineRepository) {
        this.machineRepository = machineRepository;
        this.lineRepository = lineRepository;
    }

    @Override
    @Transactional
    public MachineResponse createMachine(MachineCreateRequest request) {
        log.info("Creating machine asset with code: {}", request.machineCode());

        if (machineRepository.existsByMachineCode(request.machineCode())) {
            throw new BusinessException("DUPLICATE_MACHINE_CODE", "Machine with code '" + request.machineCode() + "' already exists");
        }

        ProductionLine line = lineRepository.findById(request.lineId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(request.lineId())));

        if (machineRepository.existsByLineIdAndStationSequence(line.getId(), request.stationSequence())) {
            throw new BusinessException("DUPLICATE_STATION_SEQUENCE",
                    String.format("Station sequence %d already assigned on line %s", request.stationSequence(), line.getLineCode()));
        }

        Machine machine = new Machine(
                line,
                request.machineCode(),
                request.machineName(),
                request.stationSequence(),
                request.assetTag(),
                request.isPacingMachine(),
                request.status() != null ? request.status() : "ACTIVE"
        );

        Machine saved = machineRepository.save(machine);
        log.info("Machine created with ID: {}", saved.getId());
        return MachineResponse.fromEntity(saved);
    }

    @Override
    public MachineResponse getMachineById(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine", String.valueOf(id)));
        return MachineResponse.fromEntity(machine);
    }

    @Override
    public MachineResponse getMachineByCode(String machineCode) {
        Machine machine = machineRepository.findByMachineCode(machineCode)
                .orElseThrow(() -> new ResourceNotFoundException("Machine", machineCode));
        return MachineResponse.fromEntity(machine);
    }

    @Override
    public List<MachineResponse> getAllMachines() {
        return machineRepository.findAll().stream()
                .map(MachineResponse::fromEntity)
                .toList();
    }

    @Override
    public List<MachineResponse> getMachinesByLine(Long lineId) {
        if (!lineRepository.existsById(lineId)) {
            throw new ResourceNotFoundException("ProductionLine", String.valueOf(lineId));
        }
        return machineRepository.findByLineIdOrderByStationSequenceAsc(lineId).stream()
                .map(MachineResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MachineResponse updateMachine(Long id, MachineUpdateRequest request) {
        log.info("Updating machine ID: {}", id);
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine", String.valueOf(id)));

        if (!machine.getStationSequence().equals(request.stationSequence()) &&
                machineRepository.existsByLineIdAndStationSequence(machine.getLine().getId(), request.stationSequence())) {
            throw new BusinessException("DUPLICATE_STATION_SEQUENCE",
                    String.format("Station sequence %d already occupied on line %s", request.stationSequence(), machine.getLine().getLineCode()));
        }

        machine.setMachineName(request.machineName());
        machine.setStationSequence(request.stationSequence());
        machine.setAssetTag(request.assetTag());
        if (request.isPacingMachine() != null) {
            machine.setIsPacingMachine(request.isPacingMachine());
        }
        if (request.status() != null) {
            machine.setStatus(request.status());
        }

        Machine updated = machineRepository.save(machine);
        return MachineResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void decommissionMachine(Long id) {
        log.info("Decommissioning machine ID: {}", id);
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine", String.valueOf(id)));

        // Soft-deactivation to preserve operational event lineage
        machine.setStatus("DECOMMISSIONED");
        machineRepository.save(machine);
        log.info("Machine ID: {} decommissioned successfully", id);
    }
}
