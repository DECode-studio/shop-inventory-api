package com.geli.shopinventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockAdjustmentRequest(
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @Size(max = 100, message = "Reference number max length is 100 characters")
        String referenceNo,

        @Size(max = 300, message = "Remarks max length is 300 characters")
        String remarks
) {}

