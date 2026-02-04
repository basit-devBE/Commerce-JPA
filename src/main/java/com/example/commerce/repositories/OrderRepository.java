package com.example.commerce.repositories;

import com.example.commerce.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT o FROM OrderEntity o WHERE " +
           "LOWER(o.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<OrderEntity> searchOrders(@Param("search") String search, Pageable pageable);
}
