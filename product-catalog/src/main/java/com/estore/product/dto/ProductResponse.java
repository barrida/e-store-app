package com.estore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String sku;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private Long categoryId;
    private String brand;
    private BigDecimal price;
    private BigDecimal salePrice;
    private BigDecimal costPrice;
    private String currency;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private BigDecimal weightKg;
    private String dimensionsCm;
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean isDigital;
    private String metadata;
    private Instant createdAt;
    private Instant updatedAt;
}
