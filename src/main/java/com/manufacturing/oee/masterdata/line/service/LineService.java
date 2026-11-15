package com.manufacturing.oee.masterdata.line.service;

import com.manufacturing.oee.masterdata.line.dto.LineCreateRequest;
import com.manufacturing.oee.masterdata.line.dto.LineResponse;
import com.manufacturing.oee.masterdata.line.dto.LineUpdateRequest;

import java.util.List;

/**
 * Service interface for ProductionLine master data operations.
 */
public interface LineService {

    LineResponse createLine(LineCreateRequest request);

    LineResponse getLineById(Long id);

    List<LineResponse> getAllLines();

    List<LineResponse> getLinesByPlantId(Long plantId);

    LineResponse updateLine(Long id, LineUpdateRequest request);

    void deactivateLine(Long id);
}
