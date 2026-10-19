package com.geli.shopinventory.dto;

import com.geli.shopinventory.domain.InventoryTransaction;
import com.geli.shopinventory.domain.Item;
import com.geli.shopinventory.domain.ItemVariant;
import com.geli.shopinventory.domain.Order;
import com.geli.shopinventory.domain.OrderItem;

import java.util.Collections;
import java.util.List;

public final class ModelMapper {

    private ModelMapper() {}

    public static VariantResponse toVariantResponse(ItemVariant variant) {
        if (variant == null) return null;
        return new VariantResponse(
                variant.getId(),
                variant.getItem() != null ? variant.getItem().getId() : null,
                variant.getItem() != null ? variant.getItem().getCode() : null,
                variant.getItem() != null ? variant.getItem().getName() : null,
                variant.getSku(),
                variant.getVariantName(),
                variant.getPrice(),
                variant.getStockQuantity(),
                variant.getStockQuantity() != null && variant.getStockQuantity() > 0,
                variant.getVersion(),
                variant.isActive(),
                variant.getCreatedAt(),
                variant.getUpdatedAt()
        );
    }

    public static ItemResponse toItemResponse(Item item, boolean includeVariants) {
        if (item == null) return null;
        List<VariantResponse> variantResponses = Collections.emptyList();
        int totalStock = 0;
        int variantsCount = 0;

        if (item.getVariants() != null) {
            variantsCount = item.getVariants().size();
            totalStock = item.getVariants().stream()
                    .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                    .sum();
            if (includeVariants) {
                variantResponses = item.getVariants().stream()
                        .map(ModelMapper::toVariantResponse)
                        .toList();
            }
        }

        return new ItemResponse(
                item.getId(),
                item.getCode(),
                item.getName(),
                item.getDescription(),
                item.getCategory(),
                item.isActive(),
                variantsCount,
                totalStock,
                variantResponses,
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    public static InventoryTransactionResponse toTransactionResponse(InventoryTransaction tx) {
        if (tx == null) return null;
        return new InventoryTransactionResponse(
                tx.getId(),
                tx.getVariant() != null ? tx.getVariant().getId() : null,
                tx.getVariant() != null ? tx.getVariant().getSku() : null,
                tx.getVariant() != null ? tx.getVariant().getVariantName() : null,
                tx.getTransactionType(),
                tx.getQuantity(),
                tx.getStockAfter(),
                tx.getReferenceNo(),
                tx.getRemarks(),
                tx.getCreatedAt()
        );
    }

    public static OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;
        List<OrderItemResponse> itemResponses = Collections.emptyList();
        int totalItemsCount = 0;

        if (order.getItems() != null) {
            totalItemsCount = order.getItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
            itemResponses = order.getItems().stream()
                    .map(item -> new OrderItemResponse(
                            item.getId(),
                            item.getVariant() != null ? item.getVariant().getId() : null,
                            item.getVariant() != null ? item.getVariant().getSku() : null,
                            item.getVariant() != null ? item.getVariant().getVariantName() : null,
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal()
                    ))
                    .toList();
        }

        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getCustomerName(),
                order.getStatus(),
                order.getTotalAmount(),
                totalItemsCount,
                itemResponses,
                order.getCreatedAt()
        );
    }
}

