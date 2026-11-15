package com.manufacturing.oee.masterdata.shift.service;

import com.manufacturing.oee.masterdata.shift.dto.ShiftInstanceCreateRequest;
import com.manufacturing.oee.masterdata.shift.dto.ShiftInstanceResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Shift Instance operational transactions.
 */
public interface ShiftInstanceService {

    ShiftInstanceResponse createShiftInstance(ShiftInstanceCreateRequest request);

    ShiftInstanceResponse getShiftInstanceById(Long id);

    ShiftInstanceResponse getShiftInstanceByCode(String code);

    List<ShiftInstanceResponse> getShiftInstancesByDate(LocalDate date);

    List<ShiftInstanceResponse> getAllShiftInstances();
}
