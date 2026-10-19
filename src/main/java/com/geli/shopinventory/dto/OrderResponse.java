package com.geli.shopinventory.dto;

import com.geli.shopinventory.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNo,
        String customerName,
        OrderStatus status,
        BigDecimal totalAmount,
        int totalItems,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {}

