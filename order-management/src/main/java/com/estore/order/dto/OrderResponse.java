package com.estore.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long userId,
        String status,
        String paymentStatus,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal shippingAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String currency,
        String shippingAddress,
        String billingAddress,
        String couponCode,
        BigDecimal couponDiscount,
        String notes,
        String ipAddress,
        String userAgent,
        Instant placedAt,
        Instant confirmedAt,
        Instant shippedAt,
        Instant deliveredAt,
        Instant cancelledAt,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {}
