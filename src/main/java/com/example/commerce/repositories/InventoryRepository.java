package com.example.commerce.repositories;

import com.example.commerce.entities.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    Optional<InventoryEntity> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    
    @Query("SELECT i FROM InventoryEntity i WHERE " +
           "LOWER(i.product.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.product.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.location) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<InventoryEntity> searchInventory(@Param("search") String search, Pageable pageable);
}
