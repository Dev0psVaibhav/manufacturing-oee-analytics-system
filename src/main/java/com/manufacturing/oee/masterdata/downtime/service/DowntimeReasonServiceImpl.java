package com.manufacturing.oee.masterdata.downtime.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonCreateRequest;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonResponse;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonUpdateRequest;
import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import com.manufacturing.oee.masterdata.downtime.repository.DowntimeReasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link DowntimeReasonService} managing downtime root cause taxonomy.
 */
@Service
@Transactional(readOnly = true)
public class DowntimeReasonServiceImpl implements DowntimeReasonService {

    private static final Logger log = LoggerFactory.getLogger(DowntimeReasonServiceImpl.class);

    private final DowntimeReasonRepository reasonRepository;

    public DowntimeReasonServiceImpl(DowntimeReasonRepository reasonRepository) {
        this.reasonRepository = reasonRepository;
    }

    @Override
    @Transactional
    public DowntimeReasonResponse createReason(DowntimeReasonCreateRequest request) {
        log.info("Creating downtime reason code: {}", request.reasonCode());

        if (reasonRepository.existsByReasonCode(request.reasonCode())) {
            throw new BusinessException("DUPLICATE_REASON_CODE", "Downtime reason code '" + request.reasonCode() + "' already exists");
        }

        DowntimeReason reason = new DowntimeReason(
                request.reasonCode(),
                request.category(),
                request.subcategory(),
                request.description(),
                request.defaultIsPlanned() != null ? request.defaultIsPlanned() : false,
                request.isActive() != null ? request.isActive() : true
        );

        DowntimeReason saved = reasonRepository.save(reason);
        log.info("Downtime reason created with ID: {}", saved.getId());
        return DowntimeReasonResponse.fromEntity(saved);
    }

    @Override
    public DowntimeReasonResponse getReasonById(Long id) {
        DowntimeReason reason = reasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", String.valueOf(id)));
        return DowntimeReasonResponse.fromEntity(reason);
    }

    @Override
    public DowntimeReasonResponse getReasonByCode(String reasonCode) {
        DowntimeReason reason = reasonRepository.findByReasonCode(reasonCode)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", reasonCode));
        return DowntimeReasonResponse.fromEntity(reason);
    }

    @Override
    public List<DowntimeReasonResponse> getAllReasons(String category, Boolean activeOnly) {
        List<DowntimeReason> reasons;
        if (category != null) {
            reasons = reasonRepository.findByCategory(category);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            reasons = reasonRepository.findByIsActiveTrue();
        } else {
            reasons = reasonRepository.findAll();
        }

        return reasons.stream()
                .map(DowntimeReasonResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public DowntimeReasonResponse updateReason(Long id, DowntimeReasonUpdateRequest request) {
        log.info("Updating downtime reason ID: {}", id);
        DowntimeReason reason = reasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", String.valueOf(id)));

        reason.setCategory(request.category());
        reason.setSubcategory(request.subcategory());
        reason.setDescription(request.description());
        if (request.defaultIsPlanned() != null) {
            reason.setDefaultIsPlanned(request.defaultIsPlanned());
        }
        if (request.isActive() != null) {
            reason.setIsActive(request.isActive());
        }

        DowntimeReason updated = reasonRepository.save(reason);
        return DowntimeReasonResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deactivateReason(Long id) {
        log.info("Deactivating downtime reason ID: {}", id);
        DowntimeReason reason = reasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", String.valueOf(id)));

        reason.setIsActive(false);
        reasonRepository.save(reason);
        log.info("Downtime reason ID: {} deactivated successfully", id);
    }
}
