package com.estore.product.repository;

import com.estore.product.model.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    Mono<Category> findBySlug(String slug);

    Flux<Category> findByParentId(Long parentId);

    Flux<Category> findByIsActiveTrue();
}
