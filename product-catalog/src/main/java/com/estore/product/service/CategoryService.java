package com.estore.product.service;

import com.estore.product.dto.CategoryRequest;
import com.estore.product.dto.CategoryResponse;
import com.estore.product.model.Category;
import com.estore.product.repository.CategoryRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Flux<CategoryResponse> findAll() {
        return categoryRepository.findAll().map(this::toResponse);
    }

    public Flux<CategoryResponse> findAllActive() {
        return categoryRepository.findByIsActiveTrue().map(this::toResponse);
    }

    public Flux<CategoryResponse> findByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId).map(this::toResponse);
    }

    public Mono<CategoryResponse> findById(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", id)))
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", slug)))
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> create(CategoryRequest request) {
        Instant now = Instant.now();
        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return categoryRepository.save(category).map(this::toResponse);
    }

    public Mono<CategoryResponse> update(Long id, CategoryRequest request) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", id)))
                .flatMap(existing -> {
                    existing.setName(request.getName());
                    existing.setSlug(request.getSlug());
                    existing.setDescription(request.getDescription());
                    existing.setParentId(request.getParentId());
                    existing.setImageUrl(request.getImageUrl());
                    if (request.getIsActive() != null) {
                        existing.setIsActive(request.getIsActive());
                    }
                    if (request.getSortOrder() != null) {
                        existing.setSortOrder(request.getSortOrder());
                    }
                    existing.setUpdatedAt(Instant.now());
                    return categoryRepository.save(existing);
                })
                .map(this::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", id)))
                .flatMap(categoryRepository::delete);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .imageUrl(category.getImageUrl())
                .isActive(category.getIsActive())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
