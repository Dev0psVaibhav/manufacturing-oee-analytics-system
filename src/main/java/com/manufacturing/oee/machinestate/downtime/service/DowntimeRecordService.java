package com.manufacturing.oee.machinestate.downtime.service;

import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordCreateRequest;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordResponse;
import com.manufacturing.oee.machinestate.downtime.dto.DowntimeRecordUpdateRequest;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for Equipment Downtime Transactions.
 */
public interface DowntimeRecordService {

    DowntimeRecordResponse startDowntime(DowntimeRecordCreateRequest request);

    DowntimeRecordResponse resolveDowntime(Long id, DowntimeRecordUpdateRequest request);

    DowntimeRecordResponse getDowntimeById(Long id);

    DowntimeRecordResponse getDowntimeByCode(String downtimeCode);

    List<DowntimeRecordResponse> searchDowntime(Long machineId, Long lineId, String status, Instant startDate, Instant endDate);
}
