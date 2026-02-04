package com.example.commerce.graphql.input;

public record ReviewInput(
        Long productId,
        Long userId,
        Integer rating,
        String comment
) {
}
