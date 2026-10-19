package com.geli.shopinventory.config;

import com.geli.shopinventory.domain.InventoryTransaction;
import com.geli.shopinventory.domain.Item;
import com.geli.shopinventory.domain.ItemVariant;
import com.geli.shopinventory.domain.TransactionType;
import com.geli.shopinventory.repository.InventoryTransactionRepository;
import com.geli.shopinventory.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ItemRepository itemRepository;
    private final InventoryTransactionRepository transactionRepository;

    public DataSeeder(ItemRepository itemRepository, InventoryTransactionRepository transactionRepository) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (itemRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data population.");
            return;
        }

        log.info("Seeding sample items and variants with warehouse stock...");

        // 1. T-Shirt
        Item tshirt = Item.builder()
                .code("TSHIRT-001")
                .name("Premium Heavyweight Cotton T-Shirt")
                .description("100% comb-bed cotton unisex t-shirt, 24s grammage")
                .category("Apparel")
                .active(true)
                .build();

        ItemVariant v1 = ItemVariant.builder()
                .sku("TSHIRT-BLK-S")
                .variantName("Black - Size S")
                .price(new BigDecimal("120000.00"))
                .stockQuantity(25)
                .active(true)
                .build();

        ItemVariant v2 = ItemVariant.builder()
                .sku("TSHIRT-BLK-M")
                .variantName("Black - Size M")
                .price(new BigDecimal("120000.00"))
                .stockQuantity(50)
                .active(true)
                .build();

        ItemVariant v3 = ItemVariant.builder()
                .sku("TSHIRT-BLK-L")
                .variantName("Black - Size L")
                .price(new BigDecimal("125000.00"))
                .stockQuantity(10)
                .active(true)
                .build();

        ItemVariant v4 = ItemVariant.builder()
                .sku("TSHIRT-WHT-M")
                .variantName("White - Size M (Out of Stock)")
                .price(new BigDecimal("120000.00"))
                .stockQuantity(0) // Out of stock sample
                .active(true)
                .build();

        tshirt.addVariant(v1);
        tshirt.addVariant(v2);
        tshirt.addVariant(v3);
        tshirt.addVariant(v4);
        Item savedTshirt = itemRepository.save(tshirt);

        // Record initial inventory logs
        for (ItemVariant v : savedTshirt.getVariants()) {
            if (v.getStockQuantity() > 0) {
                transactionRepository.save(InventoryTransaction.builder()
                        .variant(v)
                        .transactionType(TransactionType.STOCK_IN)
                        .quantity(v.getStockQuantity())
                        .stockAfter(v.getStockQuantity())
                        .referenceNo("INIT-SEED")
                        .remarks("Sample initial warehouse stock")
                        .build());
            }
        }

        // 2. Wireless Mouse
        Item mouse = Item.builder()
                .code("MOUSE-001")
                .name("Wireless Ergonomic Optical Mouse")
                .description("Dual-mode Bluetooth & 2.4G wireless mouse with silent clicks")
                .category("Electronics")
                .active(true)
                .build();

        ItemVariant v5 = ItemVariant.builder()
                .sku("MOUSE-BLK")
                .variantName("Matte Black")
                .price(new BigDecimal("350000.00"))
                .stockQuantity(15)
                .active(true)
                .build();

        ItemVariant v6 = ItemVariant.builder()
                .sku("MOUSE-SLV")
                .variantName("Silver Grey")
                .price(new BigDecimal("375000.00"))
                .stockQuantity(5)
                .active(true)
                .build();

        mouse.addVariant(v5);
        mouse.addVariant(v6);
        Item savedMouse = itemRepository.save(mouse);

        for (ItemVariant v : savedMouse.getVariants()) {
            transactionRepository.save(InventoryTransaction.builder()
                    .variant(v)
                    .transactionType(TransactionType.STOCK_IN)
                    .quantity(v.getStockQuantity())
                    .stockAfter(v.getStockQuantity())
                    .referenceNo("INIT-SEED")
                    .remarks("Sample initial warehouse stock")
                    .build());
        }

        // 3. Water Bottle
        Item bottle = Item.builder()
                .code("BOTTLE-001")
                .name("Stainless Steel Vacuum Insulated Bottle 750ml")
                .description("Keeps cold up to 24h and hot up to 12h, BPA free")
                .category("Lifestyle")
                .active(true)
                .build();

        ItemVariant v7 = ItemVariant.builder()
                .sku("BTL-750-GRN")
                .variantName("Forest Green - 750ml")
                .price(new BigDecimal("185000.00"))
                .stockQuantity(30)
                .active(true)
                .build();

        ItemVariant v8 = ItemVariant.builder()
                .sku("BTL-750-BLU")
                .variantName("Ocean Blue - 750ml (Low Stock)")
                .price(new BigDecimal("185000.00"))
                .stockQuantity(2)
                .active(true)
                .build();

        bottle.addVariant(v7);
        bottle.addVariant(v8);
        Item savedBottle = itemRepository.save(bottle);

        for (ItemVariant v : savedBottle.getVariants()) {
            transactionRepository.save(InventoryTransaction.builder()
                    .variant(v)
                    .transactionType(TransactionType.STOCK_IN)
                    .quantity(v.getStockQuantity())
                    .stockAfter(v.getStockQuantity())
                    .referenceNo("INIT-SEED")
                    .remarks("Sample initial warehouse stock")
                    .build());
        }

        log.info("Sample warehouse data seeded successfully! 3 items, 8 variants ready.");
    }
}

