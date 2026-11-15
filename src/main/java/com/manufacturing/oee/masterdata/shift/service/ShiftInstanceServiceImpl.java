package com.manufacturing.oee.masterdata.shift.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.shift.dto.ShiftInstanceCreateRequest;
import com.manufacturing.oee.masterdata.shift.dto.ShiftInstanceResponse;
import com.manufacturing.oee.masterdata.shift.entity.Shift;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.masterdata.shift.repository.ShiftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link ShiftInstanceService} managing concrete calendar shift instances.
 */
@Service
@Transactional(readOnly = true)
public class ShiftInstanceServiceImpl implements ShiftInstanceService {

    private static final Logger log = LoggerFactory.getLogger(ShiftInstanceServiceImpl.class);

    private final ShiftInstanceRepository shiftInstanceRepository;
    private final ShiftRepository shiftRepository;

    public ShiftInstanceServiceImpl(ShiftInstanceRepository shiftInstanceRepository,
                                   ShiftRepository shiftRepository) {
        this.shiftInstanceRepository = shiftInstanceRepository;
        this.shiftRepository = shiftRepository;
    }

    @Override
    @Transactional
    public ShiftInstanceResponse createShiftInstance(ShiftInstanceCreateRequest request) {
        log.info("Creating shift instance: {} for date: {}", request.shiftInstanceCode(), request.operationalDate());

        if (shiftInstanceRepository.existsByShiftInstanceCode(request.shiftInstanceCode())) {
            throw new BusinessException("DUPLICATE_SHIFT_INSTANCE_CODE",
                    "Shift instance code '" + request.shiftInstanceCode() + "' already exists");
        }

        if (shiftInstanceRepository.existsByOperationalDateAndShiftId(request.operationalDate(), request.shiftId())) {
            throw new BusinessException("DUPLICATE_OPERATIONAL_SHIFT",
                    String.format("Shift ID %d already scheduled for operational date %s", request.shiftId(), request.operationalDate()));
        }

        if (request.endTimestamp().isBefore(request.startTimestamp()) || request.endTimestamp().equals(request.startTimestamp())) {
            throw new BusinessException("INVALID_SHIFT_INTERVAL", "Shift end timestamp must be strictly after start timestamp");
        }

        Shift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift", String.valueOf(request.shiftId())));

        int gross = request.grossDurationMinutes() != null ? request.grossDurationMinutes() : 480;
        int breaks = request.plannedBreakMinutes() != null ? request.plannedBreakMinutes() : 60;
        int netPpt = gross - breaks;

        ShiftInstance instance = new ShiftInstance(
                request.shiftInstanceCode(),
                request.operationalDate(),
                shift,
                request.startTimestamp(),
                request.endTimestamp(),
                gross,
                breaks,
                netPpt
        );

        ShiftInstance saved = shiftInstanceRepository.save(instance);
        log.info("Shift instance created with ID: {}", saved.getId());
        return ShiftInstanceResponse.fromEntity(saved);
    }

    @Override
    public ShiftInstanceResponse getShiftInstanceById(Long id) {
        ShiftInstance instance = shiftInstanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance", String.valueOf(id)));
        return ShiftInstanceResponse.fromEntity(instance);
    }

    @Override
    public ShiftInstanceResponse getShiftInstanceByCode(String code) {
        ShiftInstance instance = shiftInstanceRepository.findByShiftInstanceCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance", code));
        return ShiftInstanceResponse.fromEntity(instance);
    }

    @Override
    public List<ShiftInstanceResponse> getShiftInstancesByDate(LocalDate date) {
        return shiftInstanceRepository.findByOperationalDate(date).stream()
                .map(ShiftInstanceResponse::fromEntity)
                .toList();
    }

    @Override
    public List<ShiftInstanceResponse> getAllShiftInstances() {
        return shiftInstanceRepository.findAll().stream()
                .map(ShiftInstanceResponse::fromEntity)
                .toList();
    }
}
