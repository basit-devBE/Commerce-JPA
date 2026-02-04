package com.example.commerce.graphql;

import com.example.commerce.dtos.ProductReviewStats;
import com.example.commerce.dtos.ReviewResponseDTO;
import com.example.commerce.graphql.input.ReviewInput;
import com.example.commerce.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewGraphQLController {

    private final ReviewService reviewService;

    @QueryMapping
    public List<ReviewResponseDTO> allReviews() {
        return reviewService.getAllReviews();
    }

    @QueryMapping
    public ReviewResponseDTO reviewById(@Argument Long id) {
        return reviewService.getReviewById(id);
    }

    @QueryMapping
    public List<ReviewResponseDTO> reviewsByProductId(@Argument Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @QueryMapping
    public List<ReviewResponseDTO> reviewsByUserId(@Argument Long userId) {
        return reviewService.getReviewsByUserId(userId);
    }

    @QueryMapping
    public ProductReviewStats productReviewStats(@Argument Long productId) {
        return reviewService.getProductReviewStats(productId);
    }

    @MutationMapping
    public ReviewResponseDTO addReview(@Argument ReviewInput input) {
        return reviewService.createReview(
                input.productId(),
                input.userId(),
                input.rating(),
                input.comment()
        );
    }

    @MutationMapping
    public ReviewResponseDTO updateReview(@Argument Long id, @Argument Integer rating, @Argument String comment) {
        return reviewService.updateReview(id, rating, comment);
    }

    @MutationMapping
    public Boolean deleteReview(@Argument Long id) {
        return reviewService.deleteReview(id);
    }
}
