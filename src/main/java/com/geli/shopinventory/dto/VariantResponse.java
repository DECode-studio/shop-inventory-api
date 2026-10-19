package com.geli.shopinventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VariantResponse(
        Long id,
        Long itemId,
        String itemCode,
        String itemName,
        String sku,
        String variantName,
        BigDecimal price,
        Integer stockQuantity,
        boolean inStock,
        Long version,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

