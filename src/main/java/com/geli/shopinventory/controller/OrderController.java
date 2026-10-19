package com.geli.shopinventory.controller;

import com.geli.shopinventory.dto.CheckoutRequest;
import com.geli.shopinventory.dto.OrderResponse;
import com.geli.shopinventory.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders / Checkout", description = "Endpoints for sales checkout, order processing, and anti-overselling stock deduction")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @Operation(
            summary = "Checkout / Sell items",
            description = "Processes a sale order with anti-overselling stock checks. Fails atomically if any item has insufficient stock."
    )
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        OrderResponse response = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Lists all customer sale orders sorted by latest created")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves order details including line items by primary key ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "Get order by order number", description = "Retrieves order details by unique order number")
    public ResponseEntity<OrderResponse> getOrderByOrderNo(@PathVariable String orderNo) {
        return ResponseEntity.ok(orderService.getOrderByOrderNo(orderNo));
    }
}

