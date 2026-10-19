package com.geli.shopinventory;

import com.geli.shopinventory.domain.Item;
import com.geli.shopinventory.domain.ItemVariant;
import com.geli.shopinventory.dto.StockAdjustmentRequest;
import com.geli.shopinventory.dto.VariantResponse;
import com.geli.shopinventory.exception.InsufficientStockException;
import com.geli.shopinventory.repository.ItemRepository;
import com.geli.shopinventory.repository.ItemVariantRepository;
import com.geli.shopinventory.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemVariantRepository variantRepository;

    private ItemVariant variant;

    @BeforeEach
    void setUp() {
        Item item = Item.builder()
                .code("ITEM-STOCK-TEST")
                .name("Item Stock Test")
                .active(true)
                .build();

        variant = ItemVariant.builder()
                .sku("SKU-STOCK-TEST")
                .variantName("Stock Test Variant")
                .price(new BigDecimal("50000.00"))
                .stockQuantity(20)
                .active(true)
                .build();

        item.addVariant(variant);
        itemRepository.save(item);
    }

    @Test
    @DisplayName("Should successfully add stock and update quantity")
    void shouldAddStockSuccessfully() {
        StockAdjustmentRequest req = new StockAdjustmentRequest(10, "RESTOCK-01", "Supplier shipment");
        VariantResponse response = stockService.addStock(variant.getId(), req);

        assertEquals(30, response.stockQuantity());
        assertTrue(response.inStock());
    }

    @Test
    @DisplayName("Should successfully deduct stock when requested is within balance")
    void shouldDeductStockSuccessfully() {
        StockAdjustmentRequest req = new StockAdjustmentRequest(5, "DISPATCH-01", "Damaged goods return");
        VariantResponse response = stockService.deductStock(variant.getId(), req);

        assertEquals(15, response.stockQuantity());
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when deducting more than available")
    void shouldThrowWhenDeductingMoreThanAvailable() {
        StockAdjustmentRequest req = new StockAdjustmentRequest(25, "OUT-01", "Over deduct");

        assertThrows(
                InsufficientStockException.class,
                () -> stockService.deductStock(variant.getId(), req)
        );
    }
}

