package com.estore.order.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderItemResponse(
        Long id,
        Long orderId,
        Long productId,
        String productSku,
        String productName,
        String productImageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalPrice,
        String currency,
        String productOptions,
        Instant createdAt,
        Instant updatedAt
) {}
