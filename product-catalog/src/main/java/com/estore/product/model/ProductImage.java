package com.estore.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "product_catalog", value = "product_images")
public class ProductImage {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("url")
    private String url;

    @Column("alt_text")
    private String altText;

    @Column("is_primary")
    private Boolean isPrimary;

    @Column("sort_order")
    private Integer sortOrder;

    @Column("created_at")
    private Instant createdAt;
}
