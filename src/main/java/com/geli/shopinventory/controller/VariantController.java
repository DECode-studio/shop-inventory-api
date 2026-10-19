package com.geli.shopinventory.controller;

import com.geli.shopinventory.dto.VariantRequest;
import com.geli.shopinventory.dto.VariantResponse;
import com.geli.shopinventory.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Variants", description = "Endpoints for managing product variants, pricing, and variant-level attributes")
public class VariantController {

    private final VariantService variantService;

    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @PostMapping("/api/v1/items/{itemId}/variants")
    @Operation(summary = "Create variant for an item", description = "Creates a variant (e.g. Size/Color) under a parent item with SKU and price")
    public ResponseEntity<VariantResponse> createVariant(
            @PathVariable Long itemId,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse response = variantService.createVariant(itemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/items/{itemId}/variants")
    @Operation(summary = "Get variants by item ID", description = "Lists all variants belonging to a specific item")
    public ResponseEntity<List<VariantResponse>> getVariantsByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(variantService.getVariantsByItemId(itemId));
    }

    @GetMapping("/api/v1/variants/{id}")
    @Operation(summary = "Get variant by ID", description = "Retrieves variant details by primary key ID")
    public ResponseEntity<VariantResponse> getVariantById(@PathVariable Long id) {
        return ResponseEntity.ok(variantService.getVariantById(id));
    }

    @GetMapping("/api/v1/variants/sku/{sku}")
    @Operation(summary = "Get variant by SKU", description = "Retrieves variant details by unique SKU code")
    public ResponseEntity<VariantResponse> getVariantBySku(@PathVariable String sku) {
        return ResponseEntity.ok(variantService.getVariantBySku(sku));
    }

    @PutMapping("/api/v1/variants/{id}")
    @Operation(summary = "Update a variant", description = "Updates variant name, SKU, price, or active status")
    public ResponseEntity<VariantResponse> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody VariantRequest request
    ) {
        return ResponseEntity.ok(variantService.updateVariant(id, request));
    }

    @DeleteMapping("/api/v1/variants/{id}")
    @Operation(summary = "Delete a variant", description = "Deletes a variant by ID")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }
}

