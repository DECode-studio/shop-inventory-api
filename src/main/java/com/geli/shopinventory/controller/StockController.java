package com.geli.shopinventory.controller;

import com.geli.shopinventory.dto.InventoryTransactionResponse;
import com.geli.shopinventory.dto.StockAdjustmentRequest;
import com.geli.shopinventory.dto.VariantResponse;
import com.geli.shopinventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@Tag(name = "Stocks", description = "Endpoints for inventory tracking, inbound/outbound adjustments, and audit trail")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/variants/{variantId}/add")
    @Operation(summary = "Inbound stock / Restock", description = "Adds inventory stock quantity to a variant and records a STOCK_IN movement")
    public ResponseEntity<VariantResponse> addStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        return ResponseEntity.ok(stockService.addStock(variantId, request));
    }

    @PostMapping("/variants/{variantId}/deduct")
    @Operation(summary = "Outbound stock / Manual deduction", description = "Deducts inventory stock with balance verification (prevents negative stock)")
    public ResponseEntity<VariantResponse> deductStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        return ResponseEntity.ok(stockService.deductStock(variantId, request));
    }

    @GetMapping("/variants/{variantId}")
    @Operation(summary = "Get stock by variant ID", description = "Retrieves current inventory level and status for a variant")
    public ResponseEntity<VariantResponse> getStockByVariantId(@PathVariable Long variantId) {
        return ResponseEntity.ok(stockService.getStockByVariantId(variantId));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get stock by SKU", description = "Retrieves current inventory level by SKU")
    public ResponseEntity<VariantResponse> getStockBySku(@PathVariable String sku) {
        return ResponseEntity.ok(stockService.getStockBySku(sku));
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "List out-of-stock variants", description = "Finds all variants with zero remaining inventory")
    public ResponseEntity<List<VariantResponse>> getOutOfStockVariants() {
        return ResponseEntity.ok(stockService.getOutOfStockVariants());
    }

    @GetMapping("/variants/{variantId}/transactions")
    @Operation(summary = "Get transaction history for variant", description = "Retrieves chronological audit log of all stock movements for a variant")
    public ResponseEntity<List<InventoryTransactionResponse>> getTransactionsByVariantId(@PathVariable Long variantId) {
        return ResponseEntity.ok(stockService.getTransactionsByVariantId(variantId));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all stock movement logs", description = "Retrieves all warehouse inventory transaction logs")
    public ResponseEntity<List<InventoryTransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(stockService.getAllTransactions());
    }
}

