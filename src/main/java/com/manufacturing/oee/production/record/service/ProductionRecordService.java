package com.manufacturing.oee.production.record.service;

import com.manufacturing.oee.production.record.dto.ProductionRecordCreateRequest;
import com.manufacturing.oee.production.record.dto.ProductionRecordResponse;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for Production Record transactions and telemetry.
 */
public interface ProductionRecordService {

    ProductionRecordResponse createRecord(ProductionRecordCreateRequest request);

    ProductionRecordResponse getRecordById(Long id);

    ProductionRecordResponse getRecordByCode(String recordCode);

    List<ProductionRecordResponse> searchRecords(
            Long orderId,
            Long plantId,
            Long lineId,
            Long machineId,
            Long productId,
            Long shiftInstanceId,
            Instant startDate,
            Instant endDate
    );
}
