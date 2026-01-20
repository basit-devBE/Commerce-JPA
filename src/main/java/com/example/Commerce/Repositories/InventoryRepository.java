package com.example.Commerce.Repositories;

import com.example.Commerce.Entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    Optional<InventoryEntity> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
}
