package com.example.Commerce.Repositories;

import com.example.Commerce.Entities.OrderItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {
    List<OrderItemsEntity> findByOrderId(Long orderId);
}
