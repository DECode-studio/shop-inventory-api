package com.geli.shopinventory.repository;

import com.geli.shopinventory.domain.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByCode(String code);
    boolean existsByCode(String code);
    List<Item> findByActiveTrue();

    @EntityGraph(attributePaths = {"variants"})
    Optional<Item> findWithVariantsById(Long id);

    @EntityGraph(attributePaths = {"variants"})
    Optional<Item> findWithVariantsByCode(String code);
}

