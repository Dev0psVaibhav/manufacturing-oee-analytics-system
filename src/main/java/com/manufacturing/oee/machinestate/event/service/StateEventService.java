package com.manufacturing.oee.machinestate.event.service;

import com.manufacturing.oee.machinestate.event.dto.StateEventCreateRequest;
import com.manufacturing.oee.machinestate.event.dto.StateEventResponse;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for Machine Operating State transition events.
 */
public interface StateEventService {

    StateEventResponse recordStateTransition(StateEventCreateRequest request);

    StateEventResponse getEventById(Long id);

    StateEventResponse getEventByCode(String eventCode);

    List<StateEventResponse> getMachineStateHistory(Long machineId);

    List<StateEventResponse> searchEvents(Long machineId, String state, Instant startDate, Instant endDate);
}
