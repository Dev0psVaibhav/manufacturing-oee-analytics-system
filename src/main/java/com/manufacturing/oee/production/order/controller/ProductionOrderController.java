package com.manufacturing.oee.production.order.controller;

import com.manufacturing.oee.common.constants.AppConstants;
import com.manufacturing.oee.common.response.ApiResponse;
import com.manufacturing.oee.production.order.dto.ProductionOrderCreateRequest;
import com.manufacturing.oee.production.order.dto.ProductionOrderResponse;
import com.manufacturing.oee.production.order.dto.ProductionOrderStatusUpdateRequest;
import com.manufacturing.oee.production.order.dto.ProductionOrderUpdateRequest;
import com.manufacturing.oee.production.order.service.ProductionOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Production Order lifecycle transactions.
 */
@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/production-orders")
@Tag(name = "Production Orders", description = "Endpoints for managing manufacturing work order lifecycle and scheduling")
public class ProductionOrderController {

    private final ProductionOrderService orderService;

    public ProductionOrderController(ProductionOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create production order", description = "Schedules a new production order for a given SKU on a line.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Production order created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or inactive product/line"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate order number")
    })
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> createOrder(@Valid @RequestBody ProductionOrderCreateRequest request) {
        ProductionOrderResponse created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Production order created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get all production orders", description = "Retrieves production orders optionally filtered by line ID and/or status.")
    public ResponseEntity<ApiResponse<List<ProductionOrderResponse>>> getAllOrders(
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) String status) {
        List<ProductionOrderResponse> orders = orderService.getAllOrders(lineId, status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get production order by ID", description = "Retrieves order details by surrogate database ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> getOrderById(@PathVariable Long id) {
        ProductionOrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update production order details", description = "Updates planned quantity and scheduled time windows for non-terminal orders.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update parameters or order in terminal status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionOrderUpdateRequest request) {
        ProductionOrderResponse updated = orderService.updateOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success("Production order updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update production order status", description = "Transitions order status following approved FSM lifecycle rules.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Illegal status transition"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductionOrderStatusUpdateRequest request) {
        ProductionOrderResponse updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Production order status updated", updated));
    }
}
