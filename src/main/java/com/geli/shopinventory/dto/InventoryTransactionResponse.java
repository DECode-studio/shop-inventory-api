package com.geli.shopinventory.dto;

import com.geli.shopinventory.domain.TransactionType;

import java.time.LocalDateTime;

public record InventoryTransactionResponse(
        Long id,
        Long variantId,
        String sku,
        String variantName,
        TransactionType transactionType,
        Integer quantity,
        Integer stockAfter,
        String referenceNo,
        String remarks,
        LocalDateTime createdAt
) {}

