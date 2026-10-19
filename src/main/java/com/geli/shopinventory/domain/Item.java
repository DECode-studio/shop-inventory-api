package com.geli.shopinventory.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemVariant> variants = new ArrayList<>();

    public Item() {
    }

    public Item(Long id, String code, String name, String description, String category, boolean active, List<ItemVariant> variants) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.active = active;
        if (variants != null) {
            this.variants = variants;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private String description;
        private String category;
        private boolean active = true;
        private List<ItemVariant> variants = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder variants(List<ItemVariant> variants) {
            if (variants != null) {
                this.variants = variants;
            }
            return this;
        }

        public Item build() {
            return new Item(id, code, name, description, category, active, variants);
        }
    }

    public void addVariant(ItemVariant variant) {
        variants.add(variant);
        variant.setItem(this);
    }

    public void removeVariant(ItemVariant variant) {
        variants.remove(variant);
        variant.setItem(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ItemVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ItemVariant> variants) {
        this.variants = variants;
    }
}

