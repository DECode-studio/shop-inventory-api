package com.geli.shopinventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ItemRequest(
        @NotBlank(message = "Item code cannot be blank")
        @Size(max = 50, message = "Item code max length is 50 characters")
        String code,

        @NotBlank(message = "Item name cannot be blank")
        @Size(max = 150, message = "Item name max length is 150 characters")
        String name,

        @Size(max = 500, message = "Description max length is 500 characters")
        String description,

        @Size(max = 100, message = "Category max length is 100 characters")
        String category,

        Boolean active
) {}

