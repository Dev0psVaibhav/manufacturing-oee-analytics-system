package com.manufacturing.oee.masterdata.downtime.service;

import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonCreateRequest;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonResponse;
import com.manufacturing.oee.masterdata.downtime.dto.DowntimeReasonUpdateRequest;

import java.util.List;

/**
 * Service interface for Downtime Reason taxonomy master data operations.
 */
public interface DowntimeReasonService {

    DowntimeReasonResponse createReason(DowntimeReasonCreateRequest request);

    DowntimeReasonResponse getReasonById(Long id);

    DowntimeReasonResponse getReasonByCode(String reasonCode);

    List<DowntimeReasonResponse> getAllReasons(String category, Boolean activeOnly);

    DowntimeReasonResponse updateReason(Long id, DowntimeReasonUpdateRequest request);

    void deactivateReason(Long id);
}
