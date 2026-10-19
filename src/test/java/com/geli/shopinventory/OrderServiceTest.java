package com.geli.shopinventory;

import com.geli.shopinventory.domain.*;
import com.geli.shopinventory.dto.CheckoutRequest;
import com.geli.shopinventory.dto.OrderItemRequest;
import com.geli.shopinventory.dto.OrderResponse;
import com.geli.shopinventory.exception.InsufficientStockException;
import com.geli.shopinventory.repository.InventoryTransactionRepository;
import com.geli.shopinventory.repository.ItemRepository;
import com.geli.shopinventory.repository.ItemVariantRepository;
import com.geli.shopinventory.repository.OrderRepository;
import com.geli.shopinventory.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemVariantRepository variantRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ItemVariant testVariant;

    @BeforeEach
    void setUp() {
        Item item = Item.builder()
                .code("TEST-SHIRT")
                .name("Test T-Shirt")
                .description("Test description")
                .category("Test Category")
                .active(true)
                .build();

        testVariant = ItemVariant.builder()
                .sku("TEST-VAR-1")
                .variantName("Blue - M")
                .price(new BigDecimal("100000.00"))
                .stockQuantity(10)
                .active(true)
                .build();

        item.addVariant(testVariant);
        itemRepository.save(item);
    }

    @Test
    @DisplayName("Should successfully checkout when stock is sufficient")
    void shouldSuccessfullyCheckoutWhenStockSufficient() {
        CheckoutRequest request = new CheckoutRequest(
                "John Doe",
                List.of(new OrderItemRequest(null, "TEST-VAR-1", 4))
        );

        OrderResponse orderResponse = orderService.checkout(request);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.orderNo());
        assertEquals("John Doe", orderResponse.customerName());
        assertEquals(new BigDecimal("400000.00"), orderResponse.totalAmount());
        assertEquals(4, orderResponse.totalItems());

        // Verify stock deducted
        ItemVariant updatedVariant = variantRepository.findBySku("TEST-VAR-1").orElseThrow();
        assertEquals(6, updatedVariant.getStockQuantity());
    }

    @Test
    @DisplayName("Should prevent selling items when requested quantity exceeds stock (overselling prevention)")
    void shouldThrowInsufficientStockWhenQuantityExceedsAvailable() {
        CheckoutRequest request = new CheckoutRequest(
                "Jane Doe",
                List.of(new OrderItemRequest(null, "TEST-VAR-1", 15)) // stock is only 10
        );

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> orderService.checkout(request)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock for SKU 'TEST-VAR-1'"));

        // Verify stock was not changed
        ItemVariant unchangedVariant = variantRepository.findBySku("TEST-VAR-1").orElseThrow();
        assertEquals(10, unchangedVariant.getStockQuantity());
    }

    @Test
    @DisplayName("Should prevent selling items when stock is exactly zero")
    void shouldThrowInsufficientStockWhenStockIsZero() {
        testVariant.setStockQuantity(0);
        variantRepository.save(testVariant);

        CheckoutRequest request = new CheckoutRequest(
                "Alice",
                List.of(new OrderItemRequest(null, "TEST-VAR-1", 1))
        );

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.checkout(request)
        );
    }
}

