package com.manufacturing.oee.masterdata.machine.service;

import com.manufacturing.oee.masterdata.machine.dto.MachineCreateRequest;
import com.manufacturing.oee.masterdata.machine.dto.MachineResponse;
import com.manufacturing.oee.masterdata.machine.dto.MachineUpdateRequest;

import java.util.List;

/**
 * Service interface for Machine asset master data operations.
 */
public interface MachineService {

    MachineResponse createMachine(MachineCreateRequest request);

    MachineResponse getMachineById(Long id);

    MachineResponse getMachineByCode(String machineCode);

    List<MachineResponse> getAllMachines();

    List<MachineResponse> getMachinesByLine(Long lineId);

    MachineResponse updateMachine(Long id, MachineUpdateRequest request);

    void decommissionMachine(Long id);
}
