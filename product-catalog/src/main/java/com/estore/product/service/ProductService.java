package com.estore.product.service;

import com.estore.product.dto.ProductRequest;
import com.estore.product.dto.ProductResponse;
import com.estore.product.model.Product;
import com.estore.product.repository.ProductRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Flux<ProductResponse> findAll() {
        return productRepository.findAll().map(this::toResponse);
    }

    public Flux<ProductResponse> findAllActive() {
        return productRepository.findByIsActiveTrue().map(this::toResponse);
    }

    public Flux<ProductResponse> findFeatured() {
        return productRepository.findByIsFeaturedTrue().map(this::toResponse);
    }

    public Flux<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).map(this::toResponse);
    }

    public Mono<ProductResponse> findById(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", id)))
                .map(this::toResponse);
    }

    public Mono<ProductResponse> findBySku(String sku) {
        return productRepository.findBySku(sku)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", sku)))
                .map(this::toResponse);
    }

    public Mono<ProductResponse> findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", slug)))
                .map(this::toResponse);
    }

    public Mono<ProductResponse> create(ProductRequest request) {
        Instant now = Instant.now();
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .categoryId(request.getCategoryId())
                .brand(request.getBrand())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .costPrice(request.getCostPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10)
                .weightKg(request.getWeightKg())
                .dimensionsCm(request.getDimensionsCm())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .isDigital(request.getIsDigital() != null ? request.getIsDigital() : false)
                .metadata(request.getMetadata())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return productRepository.save(product).map(this::toResponse);
    }

    public Mono<ProductResponse> update(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", id)))
                .flatMap(existing -> {
                    existing.setSku(request.getSku());
                    existing.setName(request.getName());
                    existing.setSlug(request.getSlug());
                    existing.setDescription(request.getDescription());
                    existing.setShortDescription(request.getShortDescription());
                    existing.setCategoryId(request.getCategoryId());
                    existing.setBrand(request.getBrand());
                    existing.setPrice(request.getPrice());
                    existing.setSalePrice(request.getSalePrice());
                    existing.setCostPrice(request.getCostPrice());
                    if (request.getCurrency() != null) {
                        existing.setCurrency(request.getCurrency());
                    }
                    if (request.getStockQuantity() != null) {
                        existing.setStockQuantity(request.getStockQuantity());
                    }
                    if (request.getLowStockThreshold() != null) {
                        existing.setLowStockThreshold(request.getLowStockThreshold());
                    }
                    existing.setWeightKg(request.getWeightKg());
                    existing.setDimensionsCm(request.getDimensionsCm());
                    if (request.getIsActive() != null) {
                        existing.setIsActive(request.getIsActive());
                    }
                    if (request.getIsFeatured() != null) {
                        existing.setIsFeatured(request.getIsFeatured());
                    }
                    if (request.getIsDigital() != null) {
                        existing.setIsDigital(request.getIsDigital());
                    }
                    existing.setMetadata(request.getMetadata());
                    existing.setUpdatedAt(Instant.now());
                    return productRepository.save(existing);
                })
                .map(this::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", id)))
                .flatMap(productRepository::delete);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .categoryId(product.getCategoryId())
                .brand(product.getBrand())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .costPrice(product.getCostPrice())
                .currency(product.getCurrency())
                .stockQuantity(product.getStockQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .weightKg(product.getWeightKg())
                .dimensionsCm(product.getDimensionsCm())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .isDigital(product.getIsDigital())
                .metadata(product.getMetadata())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
