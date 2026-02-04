package com.example.commerce.graphql.input;

/**
 * Input types for Cart GraphQL operations
 */
public class CartInput {
    
    public record AddToCartInput(
        Long productId,
        Integer quantity
    ) {}
    
    public record UpdateCartItemInput(
        Integer quantity
    ) {}
}
