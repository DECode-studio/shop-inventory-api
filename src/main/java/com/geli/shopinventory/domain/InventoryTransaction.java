package com.geli.shopinventory.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ItemVariant variant;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "stock_after", nullable = false)
    private Integer stockAfter;

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    @Column(name = "remarks", length = 300)
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public InventoryTransaction() {
    }

    public InventoryTransaction(Long id, ItemVariant variant, TransactionType transactionType, Integer quantity, Integer stockAfter, String referenceNo, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.variant = variant;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.stockAfter = stockAfter;
        this.referenceNo = referenceNo;
        this.remarks = remarks;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private ItemVariant variant;
        private TransactionType transactionType;
        private Integer quantity;
        private Integer stockAfter;
        private String referenceNo;
        private String remarks;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder variant(ItemVariant variant) {
            this.variant = variant;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder stockAfter(Integer stockAfter) {
            this.stockAfter = stockAfter;
            return this;
        }

        public Builder referenceNo(String referenceNo) {
            this.referenceNo = referenceNo;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InventoryTransaction build() {
            return new InventoryTransaction(id, variant, transactionType, quantity, stockAfter, referenceNo, remarks, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemVariant getVariant() {
        return variant;
    }

    public void setVariant(ItemVariant variant) {
        this.variant = variant;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(Integer stockAfter) {
        this.stockAfter = stockAfter;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

