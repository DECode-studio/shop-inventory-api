package com.geli.shopinventory.repository;

import com.geli.shopinventory.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.variant"})
    Optional<Order> findWithItemsByOrderNo(String orderNo);

    @EntityGraph(attributePaths = {"items", "items.variant"})
    Optional<Order> findWithItemsById(Long id);

    @EntityGraph(attributePaths = {"items", "items.variant"})
    List<Order> findAllByOrderByCreatedAtDesc();
}

