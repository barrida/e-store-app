package com.estore.product.repository;

import com.estore.product.model.Review;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReviewRepository extends ReactiveCrudRepository<Review, Long> {

    Flux<Review> findByProductId(Long productId);

    Flux<Review> findByUserId(Long userId);

    Flux<Review> findByProductIdAndIsApprovedTrue(Long productId);
}
