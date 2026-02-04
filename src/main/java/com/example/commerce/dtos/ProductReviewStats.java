package com.example.commerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReviewStats {
    private Long productId;
    private Double averageRating;
    private Long totalReviews;
}
