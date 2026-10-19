package com.geli.shopinventory.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long variantId,
        String sku,
        String variantName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

