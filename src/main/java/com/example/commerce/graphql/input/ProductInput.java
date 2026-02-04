package com.example.commerce.graphql.input;

/**
 * Input types for Product GraphQL operations
 */
public class ProductInput {
    
    public record AddProductInput(
        String name,
        Long categoryId,
        String sku,
        Double price
    ) {}
    
    public record UpdateProductInput(
        String name,
        Long categoryId,
        String sku,
        Double price
    ) {}
}
