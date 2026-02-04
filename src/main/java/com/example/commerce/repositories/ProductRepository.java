package com.example.commerce.repositories;

import com.example.commerce.entities.ProductEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "Product name is required") String name);
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);

    Page<ProductEntity> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    
    Page<ProductEntity> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku, Pageable pageable);
}
