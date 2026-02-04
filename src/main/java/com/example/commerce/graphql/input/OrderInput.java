package com.example.commerce.graphql.input;

import com.example.commerce.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Input types for Order GraphQL operations
 */
public class OrderInput {
    
    public record CreateOrderInput(
        @NotNull(message = "User ID is required")
        Long userId,
        
        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemInput> items
    ) {}
    
    public record OrderItemInput(
        @NotNull(message = "Product ID is required")
        Long productId,
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
    ) {}
    
    public record UpdateOrderStatusInput(
        @NotNull(message = "Status is required")
        OrderStatus status
    ) {}
}
