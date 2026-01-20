package com.example.Commerce.Repositories;

import com.example.Commerce.Entities.OrderItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {
    List<OrderItemsEntity> findByOrderId(Long orderId);
}
