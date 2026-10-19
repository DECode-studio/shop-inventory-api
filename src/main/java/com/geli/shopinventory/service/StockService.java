package com.geli.shopinventory.service;

import com.geli.shopinventory.domain.InventoryTransaction;
import com.geli.shopinventory.domain.ItemVariant;
import com.geli.shopinventory.domain.TransactionType;
import com.geli.shopinventory.dto.InventoryTransactionResponse;
import com.geli.shopinventory.dto.ModelMapper;
import com.geli.shopinventory.dto.StockAdjustmentRequest;
import com.geli.shopinventory.dto.VariantResponse;
import com.geli.shopinventory.exception.ResourceNotFoundException;
import com.geli.shopinventory.repository.InventoryTransactionRepository;
import com.geli.shopinventory.repository.ItemVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final ItemVariantRepository variantRepository;
    private final InventoryTransactionRepository transactionRepository;

    public StockService(ItemVariantRepository variantRepository, InventoryTransactionRepository transactionRepository) {
        this.variantRepository = variantRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public VariantResponse addStock(Long variantId, StockAdjustmentRequest request) {
        ItemVariant variant = variantRepository.findWithItemById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        variant.addStock(request.quantity());
        ItemVariant saved = variantRepository.save(variant);

        InventoryTransaction tx = InventoryTransaction.builder()
                .variant(saved)
                .transactionType(TransactionType.STOCK_IN)
                .quantity(request.quantity())
                .stockAfter(saved.getStockQuantity())
                .referenceNo(request.referenceNo() != null ? request.referenceNo() : "INBOUND")
                .remarks(request.remarks())
                .build();
        transactionRepository.save(tx);

        return ModelMapper.toVariantResponse(saved);
    }

    @Transactional
    public VariantResponse deductStock(Long variantId, StockAdjustmentRequest request) {
        ItemVariant variant = variantRepository.findWithItemById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        variant.deductStock(request.quantity());
        ItemVariant saved = variantRepository.save(variant);

        InventoryTransaction tx = InventoryTransaction.builder()
                .variant(saved)
                .transactionType(TransactionType.STOCK_OUT)
                .quantity(request.quantity())
                .stockAfter(saved.getStockQuantity())
                .referenceNo(request.referenceNo() != null ? request.referenceNo() : "OUTBOUND")
                .remarks(request.remarks())
                .build();
        transactionRepository.save(tx);

        return ModelMapper.toVariantResponse(saved);
    }

    @Transactional(readOnly = true)
    public VariantResponse getStockByVariantId(Long variantId) {
        ItemVariant variant = variantRepository.findWithItemById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));
        return ModelMapper.toVariantResponse(variant);
    }

    @Transactional(readOnly = true)
    public VariantResponse getStockBySku(String sku) {
        ItemVariant variant = variantRepository.findBySku(sku.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with SKU: " + sku));
        return ModelMapper.toVariantResponse(variant);
    }

    @Transactional(readOnly = true)
    public List<VariantResponse> getOutOfStockVariants() {
        return variantRepository.findByStockQuantityEquals(0).stream()
                .map(ModelMapper::toVariantResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionsByVariantId(Long variantId) {
        if (!variantRepository.existsById(variantId)) {
            throw new ResourceNotFoundException("Variant not found with id: " + variantId);
        }
        return transactionRepository.findByVariantIdOrderByCreatedAtDesc(variantId).stream()
                .map(ModelMapper::toTransactionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ModelMapper::toTransactionResponse)
                .toList();
    }
}

