package com.estore.product.controller;

import com.estore.product.dto.CategoryRequest;
import com.estore.product.dto.CategoryResponse;
import com.estore.product.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<CategoryResponse>>>> getAll() {
        return categoryService.findAll()
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.ok(list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<CategoryResponse>>> getById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(category -> ResponseEntity.ok(ApiResponse.ok(category)));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<CategoryResponse>>> create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request)
                .map(category -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.ok("Category created successfully", category)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<CategoryResponse>>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request)
                .map(category -> ResponseEntity.ok(ApiResponse.ok("Category updated successfully", category)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return categoryService.delete(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}
