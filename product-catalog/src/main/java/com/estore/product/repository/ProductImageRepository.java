package com.estore.product.repository;

import com.estore.product.model.ProductImage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductImageRepository extends ReactiveCrudRepository<ProductImage, Long> {

    Flux<ProductImage> findByProductId(Long productId);
}
