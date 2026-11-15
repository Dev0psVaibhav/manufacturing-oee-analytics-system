package com.manufacturing.oee.masterdata.shift.service;

import com.manufacturing.oee.masterdata.shift.dto.ShiftCreateRequest;
import com.manufacturing.oee.masterdata.shift.dto.ShiftResponse;

import java.util.List;

/**
 * Service interface for Shift template master data operations.
 */
public interface ShiftService {

    ShiftResponse createShift(ShiftCreateRequest request);

    ShiftResponse getShiftById(Long id);

    ShiftResponse getShiftByCode(String shiftCode);

    List<ShiftResponse> getAllShifts();
}
