package com.example.Commerce.Repositories;

import com.example.Commerce.Entities.ProductEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "Product name is required") String name);
}
