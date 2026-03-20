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
@Table(schema = "product_catalog", value = "reviews")
public class Review {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("user_id")
    private Long userId;

    @Column("rating")
    private Short rating;

    @Column("title")
    private String title;

    @Column("body")
    private String body;

    @Column("is_verified_purchase")
    private Boolean isVerifiedPurchase;

    @Column("is_approved")
    private Boolean isApproved;

    @Column("helpful_votes")
    private Integer helpfulVotes;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
