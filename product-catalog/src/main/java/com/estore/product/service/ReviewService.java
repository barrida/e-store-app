package com.estore.product.service;

import com.estore.product.dto.ReviewRequest;
import com.estore.product.dto.ReviewResponse;
import com.estore.product.model.Review;
import com.estore.product.repository.ReviewRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Flux<ReviewResponse> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId).map(this::toResponse);
    }

    public Flux<ReviewResponse> findApprovedByProductId(Long productId) {
        return reviewRepository.findByProductIdAndIsApprovedTrue(productId).map(this::toResponse);
    }

    public Flux<ReviewResponse> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId).map(this::toResponse);
    }

    public Mono<ReviewResponse> findById(Long id) {
        return reviewRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Review", id)))
                .map(this::toResponse);
    }

    public Mono<ReviewResponse> create(Long productId, ReviewRequest request) {
        Instant now = Instant.now();
        Review review = Review.builder()
                .productId(productId)
                .userId(request.getUserId())
                .rating(request.getRating())
                .title(request.getTitle())
                .body(request.getBody())
                .isVerifiedPurchase(false)
                .isApproved(false)
                .helpfulVotes(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return reviewRepository.save(review).map(this::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return reviewRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Review", id)))
                .flatMap(reviewRepository::delete);
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .title(review.getTitle())
                .body(review.getBody())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .isApproved(review.getIsApproved())
                .helpfulVotes(review.getHelpfulVotes())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
