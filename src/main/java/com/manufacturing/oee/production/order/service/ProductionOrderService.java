package com.manufacturing.oee.production.order.service;

import com.manufacturing.oee.production.order.dto.ProductionOrderCreateRequest;
import com.manufacturing.oee.production.order.dto.ProductionOrderResponse;
import com.manufacturing.oee.production.order.dto.ProductionOrderStatusUpdateRequest;
import com.manufacturing.oee.production.order.dto.ProductionOrderUpdateRequest;

import java.util.List;

/**
 * Service interface for Production Order lifecycle and management.
 */
public interface ProductionOrderService {

    ProductionOrderResponse createOrder(ProductionOrderCreateRequest request);

    ProductionOrderResponse getOrderById(Long id);

    ProductionOrderResponse getOrderByNumber(String orderNumber);

    List<ProductionOrderResponse> getAllOrders(Long lineId, String status);

    ProductionOrderResponse updateOrder(Long id, ProductionOrderUpdateRequest request);

    ProductionOrderResponse updateOrderStatus(Long id, ProductionOrderStatusUpdateRequest request);
}
