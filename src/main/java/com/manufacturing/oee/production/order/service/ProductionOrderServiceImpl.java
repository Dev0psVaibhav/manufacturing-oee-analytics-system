package com.manufacturing.oee.production.order.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.product.entity.Product;
import com.manufacturing.oee.masterdata.product.repository.ProductRepository;
import com.manufacturing.oee.production.order.dto.ProductionOrderCreateRequest;
import com.manufacturing.oee.production.order.dto.ProductionOrderResponse;
import com.manufacturing.oee.production.order.dto.ProductionOrderStatusUpdateRequest;
import com.manufacturing.oee.production.order.dto.ProductionOrderUpdateRequest;
import com.manufacturing.oee.production.order.entity.ProductionOrder;
import com.manufacturing.oee.production.order.repository.ProductionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ProductionOrderService} enforcing work order business rules and FSM state transitions.
 */
@Service
@Transactional(readOnly = true)
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private static final Logger log = LoggerFactory.getLogger(ProductionOrderServiceImpl.class);

    private final ProductionOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductionLineRepository lineRepository;

    public ProductionOrderServiceImpl(ProductionOrderRepository orderRepository,
                                     ProductRepository productRepository,
                                     ProductionLineRepository lineRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.lineRepository = lineRepository;
    }

    @Override
    @Transactional
    public ProductionOrderResponse createOrder(ProductionOrderCreateRequest request) {
        log.info("Creating production order with number: {}", request.orderNumber());

        if (orderRepository.existsByOrderNumber(request.orderNumber())) {
            throw new BusinessException("DUPLICATE_ORDER_NUMBER", "Production order with number '" + request.orderNumber() + "' already exists");
        }

        if (request.scheduledEnd().isBefore(request.scheduledStart()) || request.scheduledEnd().equals(request.scheduledStart())) {
            throw new BusinessException("INVALID_SCHEDULED_DATES", "Scheduled end timestamp must be strictly after scheduled start timestamp");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", String.valueOf(request.productId())));

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new BusinessException("INACTIVE_PRODUCT", "Cannot schedule production order for inactive product: " + product.getProductCode());
        }

        ProductionLine line = lineRepository.findById(request.lineId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine", String.valueOf(request.lineId())));

        if (!Boolean.TRUE.equals(line.getIsActive())) {
            throw new BusinessException("INACTIVE_LINE", "Cannot schedule production order for inactive production line: " + line.getLineCode());
        }

        ProductionOrder order = new ProductionOrder(
                request.orderNumber(),
                product,
                line,
                request.targetQuantity(),
                request.status() != null ? request.status() : "CREATED",
                request.scheduledStart(),
                request.scheduledEnd()
        );

        ProductionOrder saved = orderRepository.save(order);
        log.info("Production order created with ID: {}", saved.getId());
        return ProductionOrderResponse.fromEntity(saved);
    }

    @Override
    public ProductionOrderResponse getOrderById(Long id) {
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", String.valueOf(id)));
        return ProductionOrderResponse.fromEntity(order);
    }

    @Override
    public ProductionOrderResponse getOrderByNumber(String orderNumber) {
        ProductionOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", orderNumber));
        return ProductionOrderResponse.fromEntity(order);
    }

    @Override
    public List<ProductionOrderResponse> getAllOrders(Long lineId, String status) {
        List<ProductionOrder> orders;
        if (lineId != null && status != null) {
            orders = orderRepository.findByLineIdAndStatus(lineId, status);
        } else if (lineId != null) {
            orders = orderRepository.findByLineId(lineId);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(ProductionOrderResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ProductionOrderResponse updateOrder(Long id, ProductionOrderUpdateRequest request) {
        log.info("Updating production order ID: {}", id);
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", String.valueOf(id)));

        // Permitted updates only if order has not reached terminal state
        if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new BusinessException("TERMINAL_ORDER_MODIFICATION", "Cannot update production order in terminal status: " + order.getStatus());
        }

        if (request.scheduledEnd().isBefore(request.scheduledStart()) || request.scheduledEnd().equals(request.scheduledStart())) {
            throw new BusinessException("INVALID_SCHEDULED_DATES", "Scheduled end timestamp must be strictly after scheduled start timestamp");
        }

        order.setTargetQuantity(request.targetQuantity());
        order.setScheduledStart(request.scheduledStart());
        order.setScheduledEnd(request.scheduledEnd());

        if (request.actualStart() != null) {
            order.setActualStart(request.actualStart());
        }
        if (request.actualEnd() != null) {
            order.setActualEnd(request.actualEnd());
        }

        ProductionOrder updated = orderRepository.save(order);
        return ProductionOrderResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public ProductionOrderResponse updateOrderStatus(Long id, ProductionOrderStatusUpdateRequest request) {
        log.info("Transitioning status of order ID: {} to {}", id, request.status());
        ProductionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", String.valueOf(id)));

        String currentStatus = order.getStatus();
        String targetStatus = request.status();

        validateStatusTransition(currentStatus, targetStatus);

        order.setStatus(targetStatus);
        Instant timestamp = request.statusTimestamp() != null ? request.statusTimestamp() : Instant.now();

        if ("IN_PROGRESS".equals(targetStatus) && order.getActualStart() == null) {
            order.setActualStart(timestamp);
        } else if ("COMPLETED".equals(targetStatus) && order.getActualEnd() == null) {
            order.setActualEnd(timestamp);
        }

        ProductionOrder updated = orderRepository.save(order);
        log.info("Order ID: {} successfully transitioned from {} to {}", id, currentStatus, targetStatus);
        return ProductionOrderResponse.fromEntity(updated);
    }

    private void validateStatusTransition(String current, String target) {
        if (current.equals(target)) {
            return;
        }

        boolean valid = switch (current) {
            case "CREATED" -> Set.of("RELEASED", "CANCELLED").contains(target);
            case "RELEASED" -> Set.of("IN_PROGRESS", "CANCELLED").contains(target);
            case "IN_PROGRESS" -> Set.of("ON_HOLD", "COMPLETED").contains(target);
            case "ON_HOLD" -> Set.of("IN_PROGRESS", "CANCELLED").contains(target);
            case "COMPLETED", "CANCELLED" -> false; // Terminal states
            default -> false;
        };

        if (!valid) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    String.format("Invalid order lifecycle transition from '%s' to '%s'", current, target));
        }
    }
}
