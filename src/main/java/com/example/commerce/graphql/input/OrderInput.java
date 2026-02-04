package com.example.commerce.graphql.input;

import com.example.commerce.enums.OrderStatus;

import java.util.List;

/**
 * Input types for Order GraphQL operations
 */
public class OrderInput {
    
    public record CreateOrderInput(
        Long userId,
        List<OrderItemInput> items
    ) {}
    
    public record OrderItemInput(
        Long productId,
        Integer quantity
    ) {}
    
    public record UpdateOrderStatusInput(
        OrderStatus status
    ) {}
}
