package com.estore.product.controller;

import com.estore.product.dto.ProductRequest;
import com.estore.product.dto.ProductResponse;
import com.estore.product.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<ProductResponse>>>> getAll() {
        return productService.findAll()
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.ok(list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(ApiResponse.ok(product)));
    }

    @GetMapping("/sku/{sku}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> getBySku(@PathVariable String sku) {
        return productService.findBySku(sku)
                .map(product -> ResponseEntity.ok(ApiResponse.ok(product)));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.ok("Product created successfully", product)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request)
                .map(product -> ResponseEntity.ok(ApiResponse.ok("Product updated successfully", product)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return productService.delete(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}
