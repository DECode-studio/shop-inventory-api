package com.geli.shopinventory.repository;

import com.geli.shopinventory.domain.ItemVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemVariantRepository extends JpaRepository<ItemVariant, Long> {

    @EntityGraph(attributePaths = {"item"})
    Optional<ItemVariant> findWithItemById(Long id);

    @EntityGraph(attributePaths = {"item"})
    Optional<ItemVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    List<ItemVariant> findByItemId(Long itemId);

    List<ItemVariant> findByStockQuantityGreaterThan(Integer minStock);

    List<ItemVariant> findByStockQuantityEquals(Integer stock);
}

