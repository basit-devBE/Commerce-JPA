package com.example.commerce.graphql.input;

/**
 * Input types for Inventory GraphQL operations
 */
public class InventoryInput {
    
    public record AddInventoryInput(
        Long productId,
        Integer quantity,
        String location
    ) {}
    
    public record UpdateInventoryInput(
        Integer quantity,
        String location
    ) {}
}
