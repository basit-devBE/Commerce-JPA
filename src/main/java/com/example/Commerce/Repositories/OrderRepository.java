package com.example.Commerce.Repositories;

import com.example.Commerce.Entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
}
