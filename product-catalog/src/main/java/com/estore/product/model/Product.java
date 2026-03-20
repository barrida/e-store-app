package com.estore.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "product_catalog", value = "products")
public class Product {

    @Id
    private Long id;

    @Column("sku")
    private String sku;

    @Column("name")
    private String name;

    @Column("slug")
    private String slug;

    @Column("description")
    private String description;

    @Column("short_description")
    private String shortDescription;

    @Column("category_id")
    private Long categoryId;

    @Column("brand")
    private String brand;

    @Column("price")
    private BigDecimal price;

    @Column("sale_price")
    private BigDecimal salePrice;

    @Column("cost_price")
    private BigDecimal costPrice;

    @Column("currency")
    private String currency;

    @Column("stock_quantity")
    private Integer stockQuantity;

    @Column("low_stock_threshold")
    private Integer lowStockThreshold;

    @Column("weight_kg")
    private BigDecimal weightKg;

    @Column("dimensions_cm")
    private String dimensionsCm;

    @Column("is_active")
    private Boolean isActive;

    @Column("is_featured")
    private Boolean isFeatured;

    @Column("is_digital")
    private Boolean isDigital;

    @Column("metadata")
    private String metadata;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
