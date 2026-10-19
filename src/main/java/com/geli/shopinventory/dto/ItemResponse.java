package com.geli.shopinventory.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemResponse(
        Long id,
        String code,
        String name,
        String description,
        String category,
        boolean active,
        int variantsCount,
        int totalStock,
        List<VariantResponse> variants,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

