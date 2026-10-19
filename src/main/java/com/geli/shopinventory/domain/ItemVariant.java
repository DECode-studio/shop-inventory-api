package com.geli.shopinventory.domain;

import com.geli.shopinventory.exception.InsufficientStockException;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "item_variants")
public class ItemVariant extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "variant_name", nullable = false, length = 150)
    private String variantName;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public ItemVariant() {
    }

    public ItemVariant(Long id, Item item, String sku, String variantName, BigDecimal price, Integer stockQuantity, Long version, boolean active) {
        this.id = id;
        this.item = item;
        this.sku = sku;
        this.variantName = variantName;
        this.price = price;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.version = version;
        this.active = active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Item item;
        private String sku;
        private String variantName;
        private BigDecimal price;
        private Integer stockQuantity = 0;
        private Long version;
        private boolean active = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder item(Item item) {
            this.item = item;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder variantName(String variantName) {
            this.variantName = variantName;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder stockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public ItemVariant build() {
            return new ItemVariant(id, item, sku, variantName, price, stockQuantity, version, active);
        }
    }

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Deduct quantity must be greater than zero");
        }
        if (this.stockQuantity < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock for SKU '%s' (%s). Available: %d, Requested: %d",
                            this.sku, this.variantName, this.stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Add quantity must be greater than zero");
        }
        this.stockQuantity += quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

