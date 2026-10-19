package com.geli.shopinventory.repository;

import com.geli.shopinventory.domain.InventoryTransaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @EntityGraph(attributePaths = {"variant", "variant.item"})
    List<InventoryTransaction> findByVariantIdOrderByCreatedAtDesc(Long variantId);

    @EntityGraph(attributePaths = {"variant", "variant.item"})
    List<InventoryTransaction> findAllByOrderByCreatedAtDesc();
}

