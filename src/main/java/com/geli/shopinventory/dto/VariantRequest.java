package com.geli.shopinventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VariantRequest(
        @NotBlank(message = "Variant SKU cannot be blank")
        @Size(max = 50, message = "SKU max length is 50 characters")
        String sku,

        @NotBlank(message = "Variant name cannot be blank")
        @Size(max = 150, message = "Variant name max length is 150 characters")
        String variantName,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @PositiveOrZero(message = "Initial stock must be zero or positive")
        Integer initialStock,

        Boolean active
) {}

