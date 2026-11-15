package com.manufacturing.oee.machinestate.event.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.machinestate.event.dto.StateEventCreateRequest;
import com.manufacturing.oee.machinestate.event.dto.StateEventResponse;
import com.manufacturing.oee.machinestate.event.entity.StateEvent;
import com.manufacturing.oee.machinestate.event.repository.StateEventRepository;
import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import com.manufacturing.oee.masterdata.downtime.repository.DowntimeReasonRepository;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import com.manufacturing.oee.production.order.repository.ProductionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link StateEventService} managing high-frequency machine operational state history.
 */
@Service
@Transactional(readOnly = true)
public class StateEventServiceImpl implements StateEventService {

    private static final Logger log = LoggerFactory.getLogger(StateEventServiceImpl.class);

    private final StateEventRepository stateEventRepository;
    private final MachineRepository machineRepository;
    private final ProductionOrderRepository orderRepository;
    private final ShiftInstanceRepository shiftInstanceRepository;
    private final DowntimeReasonRepository downtimeReasonRepository;

    public StateEventServiceImpl(StateEventRepository stateEventRepository,
                                MachineRepository machineRepository,
                                ProductionOrderRepository orderRepository,
                                ShiftInstanceRepository shiftInstanceRepository,
                                DowntimeReasonRepository downtimeReasonRepository) {
        this.stateEventRepository = stateEventRepository;
        this.machineRepository = machineRepository;
        this.orderRepository = orderRepository;
        this.shiftInstanceRepository = shiftInstanceRepository;
        this.downtimeReasonRepository = downtimeReasonRepository;
    }

    @Override
    @Transactional
    public StateEventResponse recordStateTransition(StateEventCreateRequest request) {
        log.info("Recording machine state event: {} for machine ID: {}", request.eventCode(), request.machineId());

        if (stateEventRepository.existsByEventCode(request.eventCode())) {
            throw new BusinessException("DUPLICATE_EVENT_CODE", "State event with code '" + request.eventCode() + "' already exists");
        }

        Machine machine = machineRepository.findById(request.machineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine", String.valueOf(request.machineId())));

        ProductionOrder order = null;
        if (request.productionOrderId() != null) {
            order = orderRepository.findById(request.productionOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", String.valueOf(request.productionOrderId())));
        }

        ShiftInstance shiftInstance = null;
        if (request.shiftInstanceId() != null) {
            shiftInstance = shiftInstanceRepository.findById(request.shiftInstanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance", String.valueOf(request.shiftInstanceId())));
        }

        DowntimeReason downtimeReason = null;
        if ("UNPLANNED_DOWNTIME".equals(request.state())) {
            if (request.downtimeReasonId() == null) {
                throw new BusinessException("MISSING_DOWNTIME_REASON", "Downtime reason is mandatory when transitioning to UNPLANNED_DOWNTIME");
            }
            downtimeReason = downtimeReasonRepository.findById(request.downtimeReasonId())
                    .orElseThrow(() -> new ResourceNotFoundException("DowntimeReason", String.valueOf(request.downtimeReasonId())));
        } else if (request.downtimeReasonId() != null) {
            downtimeReason = downtimeReasonRepository.findById(request.downtimeReasonId()).orElse(null);
        }

        if (request.endTime() != null && request.endTime().isBefore(request.startTime())) {
            throw new BusinessException("INVALID_EVENT_WINDOW", "Event end timestamp cannot precede start timestamp");
        }

        // Determine previous state by inspecting previous latest event on this machine
        Optional<StateEvent> latestEventOpt = stateEventRepository.findTopByMachineIdOrderByStartTimeDesc(machine.getId());
        String previousState = "UNKNOWN";

        if (latestEventOpt.isPresent()) {
            StateEvent latestEvent = latestEventOpt.get();
            previousState = latestEvent.getState();

            // Auto-close previous event if still open
            if (latestEvent.getEndTime() == null) {
                latestEvent.setEndTime(request.startTime());
                stateEventRepository.save(latestEvent);
                log.info("Closed previous open event ID: {} on machine ID: {} at {}", latestEvent.getId(), machine.getId(), request.startTime());
            }
        }

        StateEvent event = new StateEvent(
                request.eventCode(),
                machine,
                order,
                shiftInstance,
                previousState,
                request.state(),
                request.startTime(),
                request.endTime(),
                downtimeReason,
                request.source()
        );

        StateEvent saved = stateEventRepository.save(event);
        log.info("Recorded state event ID: {} ({} -> {})", saved.getId(), previousState, saved.getState());
        return StateEventResponse.fromEntity(saved);
    }

    @Override
    public StateEventResponse getEventById(Long id) {
        StateEvent event = stateEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StateEvent", String.valueOf(id)));
        return StateEventResponse.fromEntity(event);
    }

    @Override
    public StateEventResponse getEventByCode(String eventCode) {
        StateEvent event = stateEventRepository.findByEventCode(eventCode)
                .orElseThrow(() -> new ResourceNotFoundException("StateEvent", eventCode));
        return StateEventResponse.fromEntity(event);
    }

    @Override
    public List<StateEventResponse> getMachineStateHistory(Long machineId) {
        if (!machineRepository.existsById(machineId)) {
            throw new ResourceNotFoundException("Machine", String.valueOf(machineId));
        }
        return stateEventRepository.findByMachineIdOrderByStartTimeDesc(machineId).stream()
                .map(StateEventResponse::fromEntity)
                .toList();
    }

    @Override
    public List<StateEventResponse> searchEvents(Long machineId, String state, Instant startDate, Instant endDate) {
        return stateEventRepository.findEventsWithFilters(machineId, state, startDate, endDate).stream()
                .map(StateEventResponse::fromEntity)
                .toList();
    }
}
