package com.geli.shopinventory.service;

import com.geli.shopinventory.domain.InventoryTransaction;
import com.geli.shopinventory.domain.Item;
import com.geli.shopinventory.domain.ItemVariant;
import com.geli.shopinventory.domain.TransactionType;
import com.geli.shopinventory.dto.ModelMapper;
import com.geli.shopinventory.dto.VariantRequest;
import com.geli.shopinventory.dto.VariantResponse;
import com.geli.shopinventory.exception.DuplicateResourceException;
import com.geli.shopinventory.exception.ResourceNotFoundException;
import com.geli.shopinventory.repository.InventoryTransactionRepository;
import com.geli.shopinventory.repository.ItemRepository;
import com.geli.shopinventory.repository.ItemVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VariantService {

    private final ItemVariantRepository variantRepository;
    private final ItemRepository itemRepository;
    private final InventoryTransactionRepository transactionRepository;

    public VariantService(ItemVariantRepository variantRepository, ItemRepository itemRepository, InventoryTransactionRepository transactionRepository) {
        this.variantRepository = variantRepository;
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public VariantResponse createVariant(Long itemId, VariantRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        String sku = request.sku().trim().toUpperCase();
        if (variantRepository.existsBySku(sku)) {
            throw new DuplicateResourceException("Variant with SKU '" + sku + "' already exists");
        }

        int initialStock = request.initialStock() != null ? request.initialStock() : 0;

        ItemVariant variant = ItemVariant.builder()
                .item(item)
                .sku(sku)
                .variantName(request.variantName().trim())
                .price(request.price())
                .stockQuantity(initialStock)
                .active(request.active() == null || request.active())
                .build();

        ItemVariant savedVariant = variantRepository.save(variant);

        // Record initial inventory transaction if initialStock > 0
        if (initialStock > 0) {
            InventoryTransaction initialTx = InventoryTransaction.builder()
                    .variant(savedVariant)
                    .transactionType(TransactionType.STOCK_IN)
                    .quantity(initialStock)
                    .stockAfter(initialStock)
                    .referenceNo("INIT-STOCK")
                    .remarks("Initial stock upon variant creation")
                    .build();
            transactionRepository.save(initialTx);
        }

        return ModelMapper.toVariantResponse(savedVariant);
    }

    @Transactional(readOnly = true)
    public VariantResponse getVariantById(Long id) {
        ItemVariant variant = variantRepository.findWithItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + id));
        return ModelMapper.toVariantResponse(variant);
    }

    @Transactional(readOnly = true)
    public VariantResponse getVariantBySku(String sku) {
        ItemVariant variant = variantRepository.findBySku(sku.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with SKU: " + sku));
        return ModelMapper.toVariantResponse(variant);
    }

    @Transactional(readOnly = true)
    public List<VariantResponse> getVariantsByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found with id: " + itemId);
        }
        return variantRepository.findByItemId(itemId).stream()
                .map(ModelMapper::toVariantResponse)
                .toList();
    }

    @Transactional
    public VariantResponse updateVariant(Long id, VariantRequest request) {
        ItemVariant variant = variantRepository.findWithItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + id));

        String newSku = request.sku().trim().toUpperCase();
        if (!variant.getSku().equalsIgnoreCase(newSku) && variantRepository.existsBySku(newSku)) {
            throw new DuplicateResourceException("Variant with SKU '" + newSku + "' already exists");
        }

        variant.setSku(newSku);
        variant.setVariantName(request.variantName().trim());
        variant.setPrice(request.price());
        if (request.active() != null) {
            variant.setActive(request.active());
        }

        ItemVariant updated = variantRepository.save(variant);
        return ModelMapper.toVariantResponse(updated);
    }

    @Transactional
    public void deleteVariant(Long id) {
        ItemVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + id));
        variantRepository.delete(variant);
    }
}

