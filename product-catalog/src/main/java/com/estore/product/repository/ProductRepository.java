package com.estore.product.repository;

import com.estore.product.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Mono<Product> findBySku(String sku);

    Mono<Product> findBySlug(String slug);

    Flux<Product> findByCategoryId(Long categoryId);

    Flux<Product> findByIsActiveTrue();

    Flux<Product> findByIsFeaturedTrue();
}
