package com.manufacturing.oee.machinestate.downtime.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordCreateRequest;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordResponse;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordUpdateRequest;
import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import com.manufacturing.oee.machinestate.downtime.repository.DowntimeRecordRepository;
import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import com.manufacturing.oee.masterdata.downtime.repository.DowntimeReasonRepository;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import com.manufacturing.oee.production.order.repository.ProductionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Implementation of {@link DowntimeRecordService} tracking equipment downtime events and durations.
 */
@Service
@Transactional(readOnly = true)
public class DowntimeRecordServiceImpl implements DowntimeRecordService {

    private static final Logger log = LoggerFactory.getLogger(DowntimeRecordServiceImpl.class);

    private final DowntimeRecordRepository downtimeRepository;
    private final MachineRepository machineRepository;
    private final ProductionLineRepository lineRepository;
    private final DowntimeReasonRepository reasonRepository;
    private final ProductionOrderRepository orderRepository;
    private final ShiftInstanceRepository shiftInstanceRepository;

    public DowntimeRecordServiceImpl(DowntimeRecordRepository downtimeRepository,
                                     MachineRepository machineRepository,
                                     ProductionLineRepository lineRepository,
                                     DowntimeReasonRepository reasonRepository,
                                     ProductionOrderRepository orderRepository,
                                     ShiftInstanceRepository shiftInstanceRepository) {
        this.downtimeRepository = downtimeRepository;
        this.machineRepository = machineRepository;
        this.lineRepository = lineRepository;
        this.reasonRepository = reasonRepository;
        this.orderRepository = orderRepository;
        this.shiftInstanceRepository = shiftInstanceRepository;
    }

    @Override
    @Transactional
    public DowntimeRecordResponse startDowntime(DowntimeRecordCreateRequest request) {
        log.info("Initiating downtime record: {} on machine ID: {}", request.downtimeCode(), request.machineId());

        if (downtimeRepository.existsByDowntimeCode(request.downtimeCode())) {
            throw new BusinessException("DUPLICATE_DOWNTIME_CODE", "Downtime record with code '" + request.downtimeCode() + "' already exists");
        }

        Machine machine = machineRepository.findById(request.machineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine", String.valueOf(request.machineId())));

        ProductionLine line = lineRepository.findById(request.lineId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(request.lineId())));

        if (!machine.getLine().getId().equals(line.getId())) {
            throw new BusinessException("MACHINE_LINE_MISMATCH",
                    String.format("Machine line ID (%d) does not match requested line ID (%d)", machine.getLine().getId(), line.getId()));
        }

        DowntimeReason reason = reasonRepository.findById(request.reasonId())
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", String.valueOf(request.reasonId())));

        if (!Boolean.TRUE.equals(reason.getIsActive())) {
            throw new BusinessException("INACTIVE_DOWNTIME_REASON", "Cannot assign inactive downtime reason: " + reason.getReasonCode());
        }

        ProductionOrder order = null;
        if (request.productionOrderId() != null) {
            order = orderRepository.findById(request.productionOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", String.valueOf(request.productionOrderId())));
        }

        ShiftInstance shiftInstance = null;
        if (request.shiftInstanceId() != null) {
            shiftInstance = shiftInstanceRepository.findById(request.shiftInstanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance", String.valueOf(request.shiftInstanceId())));
        }

        String status = "OPEN";
        if (request.endTime() != null) {
            if (request.endTime().isBefore(request.startTime())) {
                throw new BusinessException("INVALID_DOWNTIME_WINDOW", "Downtime end time cannot precede start time");
            }
            status = "RESOLVED";
        }

        DowntimeRecord record = new DowntimeRecord(
                request.downtimeCode(),
                machine,
                line,
                reason,
                order,
                shiftInstance,
                request.startTime(),
                request.endTime(),
                request.description(),
                status
        );

        DowntimeRecord saved = downtimeRepository.save(record);
        log.info("Downtime record created with ID: {} (Status: {})", saved.getId(), saved.getStatus());
        return DowntimeRecordResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public DowntimeRecordResponse resolveDowntime(Long id, DowntimeRecordUpdateRequest request) {
        log.info("Resolving downtime record ID: {}", id);
        DowntimeRecord record = downtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeRecord", String.valueOf(id)));

        if (request.endTime().isBefore(record.getStartTime())) {
            throw new BusinessException("INVALID_DOWNTIME_WINDOW", "Downtime end time cannot precede start time");
        }

        record.setEndTime(request.endTime());
        record.setDurationSeconds((int) Duration.between(record.getStartTime(), request.endTime()).getSeconds());
        record.setStatus("RESOLVED");

        if (request.reasonId() != null) {
            DowntimeReason reason = reasonRepository.findById(request.reasonId())
                    .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", String.valueOf(request.reasonId())));
            record.setReason(reason);
        }

        if (request.description() != null) {
            record.setDescription(request.description());
        }

        DowntimeRecord updated = downtimeRepository.save(record);
        log.info("Downtime record ID: {} resolved with duration: {} seconds", updated.getId(), updated.getDurationSeconds());
        return DowntimeRecordResponse.fromEntity(updated);
    }

    @Override
    public DowntimeRecordResponse getDowntimeById(Long id) {
        DowntimeRecord record = downtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeRecord", String.valueOf(id)));
        return DowntimeRecordResponse.fromEntity(record);
    }

    @Override
    public DowntimeRecordResponse getDowntimeByCode(String downtimeCode) {
        DowntimeRecord record = downtimeRepository.findByDowntimeCode(downtimeCode)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeRecord", downtimeCode));
        return DowntimeRecordResponse.fromEntity(record);
    }

    @Override
    public List<DowntimeRecordResponse> searchDowntime(Long machineId, Long lineId, String status, Instant startDate, Instant endDate) {
        return downtimeRepository.findDowntimeWithFilters(machineId, lineId, status, startDate, endDate).stream()
                .map(DowntimeRecordResponse::fromEntity)
                .toList();
    }
}
