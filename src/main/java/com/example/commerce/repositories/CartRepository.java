package com.example.commerce.repositories;

import com.example.commerce.entities.CartEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    @EntityGraph(attributePaths = {"items", "items.product", "items.product.category", "user"})
    Optional<CartEntity> findByUserId(Long userId);
}
