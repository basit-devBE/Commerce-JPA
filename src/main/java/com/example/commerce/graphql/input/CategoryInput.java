package com.example.commerce.graphql.input;

/**
 * Input types for Category GraphQL operations
 */
public class CategoryInput {
    
    public record AddCategoryInput(
        String name,
        String description
    ) {}
    
    public record UpdateCategoryInput(
        String name,
        String description
    ) {}
}
