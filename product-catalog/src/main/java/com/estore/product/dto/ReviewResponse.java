package com.estore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long productId;
    private Long userId;
    private Short rating;
    private String title;
    private String body;
    private Boolean isVerifiedPurchase;
    private Boolean isApproved;
    private Integer helpfulVotes;
    private Instant createdAt;
    private Instant updatedAt;
}
