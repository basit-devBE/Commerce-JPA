package com.example.commerce.repositories;

import com.example.commerce.entities.OrderItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {
    List<OrderItemsEntity> findByOrderId(Long orderId);
}
