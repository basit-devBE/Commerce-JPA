package com.example.commerce.repositories;

import com.example.commerce.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    
    Page<ReviewEntity> findByProductId(Long productId, Pageable pageable);
    
    List<ReviewEntity> findByProductId(Long productId);
    
    List<ReviewEntity> findByUserId(Long userId);
    
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(Long productId);
    
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.product.id = :productId")
    Long getReviewCountByProductId(Long productId);
}
