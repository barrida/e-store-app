package com.estore.product.controller;

import com.estore.product.dto.ReviewRequest;
import com.estore.product.dto.ReviewResponse;
import com.estore.product.service.ReviewService;
import com.estore.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/products/{productId}/reviews")
    public Mono<ResponseEntity<ApiResponse<List<ReviewResponse>>>> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.findByProductId(productId)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.ok(list)));
    }

    @PostMapping("/api/products/{productId}/reviews")
    public Mono<ResponseEntity<ApiResponse<ReviewResponse>>> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(productId, request)
                .map(review -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.ok("Review created successfully", review)));
    }

    @DeleteMapping("/api/reviews/{id}")
    public Mono<ResponseEntity<Void>> deleteReview(@PathVariable Long id) {
        return reviewService.delete(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}
