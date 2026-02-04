package com.example.commerce.services;

import com.example.commerce.dtos.ProductReviewStats;
import com.example.commerce.dtos.ReviewResponseDTO;
import com.example.commerce.entities.ProductEntity;
import com.example.commerce.entities.ReviewEntity;
import com.example.commerce.entities.UserEntity;
import com.example.commerce.errorhandlers.ResourceAlreadyExists;
import com.example.commerce.errorhandlers.ResourceNotFoundException;
import com.example.commerce.mappers.ReviewMapper;
import com.example.commerce.repositories.ProductRepository;
import com.example.commerce.repositories.ReviewRepository;
import com.example.commerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewResponseDTO createReview(Long productId, Long userId, Integer rating, String comment) {
        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new ResourceAlreadyExists("You have already reviewed this product");
        }

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        ReviewEntity review = new ReviewEntity();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);

        ReviewEntity savedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(savedReview);
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, Integer rating, String comment) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            review.setRating(rating);
        }

        if (comment != null) {
            review.setComment(comment);
        }

        ReviewEntity updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }

    @Transactional
    public boolean deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
        return true;
    }

    public ReviewResponseDTO getReviewById(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        return reviewMapper.toDTO(review);
    }

    public List<ReviewResponseDTO> getReviewsByProductId(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ReviewResponseDTO> getReviewsByProductIdPaginated(Long productId, Pageable pageable) {
        Page<ReviewEntity> reviews = reviewRepository.findByProductId(productId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        List<ReviewEntity> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductReviewStats getProductReviewStats(Long productId) {
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.getReviewCountByProductId(productId);

        return new ProductReviewStats(
                productId,
                averageRating != null ? averageRating : 0.0,
                totalReviews
        );
    }

    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }
}
