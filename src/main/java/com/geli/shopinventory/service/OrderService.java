package com.geli.shopinventory.service;

import com.geli.shopinventory.domain.*;
import com.geli.shopinventory.dto.*;
import com.geli.shopinventory.exception.InsufficientStockException;
import com.geli.shopinventory.exception.ResourceNotFoundException;
import com.geli.shopinventory.repository.InventoryTransactionRepository;
import com.geli.shopinventory.repository.ItemVariantRepository;
import com.geli.shopinventory.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final ItemVariantRepository variantRepository;
    private final OrderRepository orderRepository;
    private final InventoryTransactionRepository transactionRepository;

    public OrderService(ItemVariantRepository variantRepository, OrderRepository orderRepository, InventoryTransactionRepository transactionRepository) {
        this.variantRepository = variantRepository;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        String orderNo = generateOrderNo();

        Order order = Order.builder()
                .orderNo(orderNo)
                .customerName(request.customerName().trim())
                .status(OrderStatus.COMPLETED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            ItemVariant variant = resolveVariant(itemReq);

            if (!variant.isActive() || (variant.getItem() != null && !variant.getItem().isActive())) {
                throw new InsufficientStockException(
                        String.format("Variant '%s' (%s) is not active and cannot be sold",
                                variant.getSku(), variant.getVariantName())
                );
            }

            // ATOMIC STOCK DEDUCTION WITH OPTIMISTIC LOCKING
            // Throws InsufficientStockException if requested > stockQuantity
            variant.deductStock(itemReq.quantity());
            variantRepository.save(variant);

            BigDecimal unitPrice = variant.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            order.addItem(orderItem);

            // Record sale inventory deduction in audit trail
            InventoryTransaction tx = InventoryTransaction.builder()
                    .variant(variant)
                    .transactionType(TransactionType.SALE_DEDUCTION)
                    .quantity(itemReq.quantity())
                    .stockAfter(variant.getStockQuantity())
                    .referenceNo(orderNo)
                    .remarks("Sale checkout for customer: " + request.customerName())
                    .build();
            transactionRepository.save(tx);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return ModelMapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return ModelMapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNo(String orderNo) {
        Order order = orderRepository.findWithItemsByOrderNo(orderNo.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderNo: " + orderNo));
        return ModelMapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ModelMapper::toOrderResponse)
                .toList();
    }

    private ItemVariant resolveVariant(OrderItemRequest itemReq) {
        if (itemReq.variantId() != null) {
            return variantRepository.findWithItemById(itemReq.variantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + itemReq.variantId()));
        } else if (itemReq.sku() != null && !itemReq.sku().isBlank()) {
            return variantRepository.findBySku(itemReq.sku().trim().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with SKU: " + itemReq.sku()));
        } else {
            throw new IllegalArgumentException("Either variantId or sku must be provided in order item");
        }
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp + "-" + randomSuffix;
    }
}

