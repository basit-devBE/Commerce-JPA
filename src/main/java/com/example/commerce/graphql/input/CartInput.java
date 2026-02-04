package com.example.commerce.graphql.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Input types for Cart GraphQL operations
 */
public class CartInput {
    
    public record AddToCartInput(
        @NotNull(message = "Product ID is required")
        Long productId,
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
    ) {}
    
    public record UpdateCartItemInput(
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity
    ) {}
}
