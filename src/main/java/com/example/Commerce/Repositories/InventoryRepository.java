package com.example.Commerce.Repositories;

import com.example.Commerce.Entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    Optional<InventoryEntity> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
}
