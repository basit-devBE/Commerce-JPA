package com.example.commerce.repositories;

import com.example.commerce.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteByCartId(Long cartId);
}
