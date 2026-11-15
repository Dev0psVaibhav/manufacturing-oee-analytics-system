package com.manufacturing.oee.production.record.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.plant.repository.PlantRepository;
import com.manufacturing.oee.masterdata.product.entity.Product;
import com.manufacturing.oee.masterdata.product.repository.ProductRepository;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import com.manufacturing.oee.production.order.repository.ProductionOrderRepository;
import com.manufacturing.oee.production.record.dto.ProductionRecordCreateRequest;
import com.manufacturing.oee.production.record.dto.ProductionRecordResponse;
import com.manufacturing.oee.production.record.entity.ProductionRecord;
import com.manufacturing.oee.production.record.repository.ProductionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Implementation of {@link ProductionRecordService} enforcing quantity conservation and referential alignment.
 */
@Service
@Transactional(readOnly = true)
public class ProductionRecordServiceImpl implements ProductionRecordService {

    private static final Logger log = LoggerFactory.getLogger(ProductionRecordServiceImpl.class);

    private final ProductionRecordRepository recordRepository;
    private final ProductionOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PlantRepository plantRepository;
    private final ProductionLineRepository lineRepository;
    private final MachineRepository machineRepository;
    private final ShiftInstanceRepository shiftInstanceRepository;

    public ProductionRecordServiceImpl(ProductionRecordRepository recordRepository,
                                       ProductionOrderRepository orderRepository,
                                       ProductRepository productRepository,
                                       PlantRepository plantRepository,
                                       ProductionLineRepository lineRepository,
                                       MachineRepository machineRepository,
                                       ShiftInstanceRepository shiftInstanceRepository) {
        this.recordRepository = recordRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.plantRepository = plantRepository;
        this.lineRepository = lineRepository;
        this.machineRepository = machineRepository;
        this.shiftInstanceRepository = shiftInstanceRepository;
    }

    @Override
    @Transactional
    public ProductionRecordResponse createRecord(ProductionRecordCreateRequest request) {
        log.info("Recording production count with code: {}", request.recordCode());

        if (recordRepository.existsByRecordCode(request.recordCode())) {
            throw new BusinessException("DUPLICATE_RECORD_CODE", "Production record with code '" + request.recordCode() + "' already exists");
        }

        // Strict quantity conservation per BR-CNT-001: Total = Good + Rejected
        if (request.totalQuantity() != (request.goodQuantity() + request.rejectedQuantity())) {
            throw new BusinessException("QUANTITY_CONSERVATION_VIOLATION",
                    String.format("Total quantity (%d) must equal good quantity (%d) plus rejected quantity (%d)",
                            request.totalQuantity(), request.goodQuantity(), request.rejectedQuantity()));
        }

        ProductionOrder order = orderRepository.findById(request.productionOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", String.valueOf(request.productionOrderId())));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", String.valueOf(request.productId())));

        Plant plant = plantRepository.findById(request.plantId())
                .orElseThrow(() -> new ResourceNotFoundException("Plant", String.valueOf(request.plantId())));

        ProductionLine line = lineRepository.findById(request.lineId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(request.lineId())));

        Machine machine = machineRepository.findById(request.machineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine", String.valueOf(request.machineId())));

        ShiftInstance shiftInstance = shiftInstanceRepository.findById(request.shiftInstanceId())
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance", String.valueOf(request.shiftInstanceId())));

        // Cross-entity integrity checks
        if (!order.getProduct().getId().equals(product.getId())) {
            throw new BusinessException("ORDER_PRODUCT_MISMATCH",
                    String.format("Order product ID (%d) does not match record product ID (%d)", order.getProduct().getId(), product.getId()));
        }

        if (!order.getLine().getId().equals(line.getId())) {
            throw new BusinessException("ORDER_LINE_MISMATCH",
                    String.format("Order line ID (%d) does not match record line ID (%d)", order.getLine().getId(), line.getId()));
        }

        if (!machine.getLine().getId().equals(line.getId())) {
            throw new BusinessException("MACHINE_LINE_MISMATCH",
                    String.format("Machine line ID (%d) does not match record line ID (%d)", machine.getLine().getId(), line.getId()));
        }

        if (!line.getPlant().getId().equals(plant.getId())) {
            throw new BusinessException("LINE_PLANT_MISMATCH",
                    String.format("Line plant ID (%d) does not match record plant ID (%d)", line.getPlant().getId(), plant.getId()));
        }

        ProductionRecord record = new ProductionRecord(
                request.recordCode(),
                order,
                product,
                plant,
                line,
                machine,
                shiftInstance,
                request.productionTimestamp(),
                request.totalQuantity(),
                request.goodQuantity(),
                request.rejectedQuantity(),
                request.notes()
        );

        ProductionRecord saved = recordRepository.save(record);
        log.info("Production record created with ID: {}", saved.getId());
        return ProductionRecordResponse.fromEntity(saved);
    }

    @Override
    public ProductionRecordResponse getRecordById(Long id) {
        ProductionRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", String.valueOf(id)));
        return ProductionRecordResponse.fromEntity(record);
    }

    @Override
    public ProductionRecordResponse getRecordByCode(String recordCode) {
        ProductionRecord record = recordRepository.findByRecordCode(recordCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", recordCode));
        return ProductionRecordResponse.fromEntity(record);
    }

    @Override
    public List<ProductionRecordResponse> searchRecords(Long orderId, Long plantId, Long lineId, Long machineId,
                                                        Long productId, Long shiftInstanceId, Instant startDate, Instant endDate) {
        List<ProductionRecord> records = recordRepository.findRecordsWithFilters(
                orderId, plantId, lineId, machineId, productId, shiftInstanceId, startDate, endDate);
        return records.stream()
                .map(ProductionRecordResponse::fromEntity)
                .toList();
    }
}
