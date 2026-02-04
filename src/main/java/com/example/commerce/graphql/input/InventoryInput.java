package com.example.commerce.graphql.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Input types for Inventory GraphQL operations
 */
public class InventoryInput {
    
    public record AddInventoryInput(
        @NotNull(message = "Product ID is required")
        Long productId,
        
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,
        
        @NotBlank(message = "Location is required")
        String location
    ) {}
    
    public record UpdateInventoryInput(
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,
        
        String location
    ) {}
}
