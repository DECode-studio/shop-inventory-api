package com.geli.shopinventory.controller;

import com.geli.shopinventory.dto.ItemRequest;
import com.geli.shopinventory.dto.ItemResponse;
import com.geli.shopinventory.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items", description = "Endpoints for managing shop catalog items (parent products)")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Operation(summary = "Create a new item", description = "Creates a top-level product item in the warehouse catalog")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request) {
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieves list of items with summary of variants and stock")
    public ResponseEntity<List<ItemResponse>> getAllItems(@RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(itemService.getAllItems(activeOnly));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieves an item and its associated variants by primary key ID")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get item by code", description = "Retrieves an item by unique business code")
    public ResponseEntity<ItemResponse> getItemByCode(@PathVariable String code) {
        return ResponseEntity.ok(itemService.getItemByCode(code));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item", description = "Updates details of an existing catalog item")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return ResponseEntity.ok(itemService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item", description = "Deletes an item and cascades to its variants")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}

