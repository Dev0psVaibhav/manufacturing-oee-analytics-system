package com.manufacturing.oee.masterdata.shift.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.shift.dto.ShiftCreateRequest;
import com.manufacturing.oee.masterdata.shift.dto.ShiftResponse;
import com.manufacturing.oee.masterdata.shift.entity.Shift;
import com.manufacturing.oee.masterdata.shift.repository.ShiftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link ShiftService} managing recurring 8-hour shift templates.
 */
@Service
@Transactional(readOnly = true)
public class ShiftServiceImpl implements ShiftService {

    private static final Logger log = LoggerFactory.getLogger(ShiftServiceImpl.class);

    private final ShiftRepository shiftRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    @Transactional
    public ShiftResponse createShift(ShiftCreateRequest request) {
        log.info("Creating shift template with code: {}", request.shiftCode());

        if (shiftRepository.existsByShiftCode(request.shiftCode())) {
            throw new BusinessException("DUPLICATE_SHIFT_CODE", "Shift with code '" + request.shiftCode() + "' already exists");
        }

        Shift shift = new Shift(
                request.shiftCode(),
                request.shiftName(),
                request.defaultStartTime(),
                request.defaultEndTime(),
                request.spansMidnight() != null ? request.spansMidnight() : false
        );

        Shift saved = shiftRepository.save(shift);
        log.info("Shift template created with ID: {}", saved.getId());
        return ShiftResponse.fromEntity(saved);
    }

    @Override
    public ShiftResponse getShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", String.valueOf(id)));
        return ShiftResponse.fromEntity(shift);
    }

    @Override
    public ShiftResponse getShiftByCode(String shiftCode) {
        Shift shift = shiftRepository.findByShiftCode(shiftCode)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", shiftCode));
        return ShiftResponse.fromEntity(shift);
    }

    @Override
    public List<ShiftResponse> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(ShiftResponse::fromEntity)
                .toList();
    }
}
