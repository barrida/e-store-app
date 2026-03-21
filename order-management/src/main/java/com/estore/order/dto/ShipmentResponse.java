package com.estore.order.dto;

import java.time.Instant;
import java.time.LocalDate;

public record ShipmentResponse(
        Long id,
        Long orderId,
        String trackingNumber,
        String carrier,
        String shippingMethod,
        LocalDate estimatedDelivery,
        Instant shippedAt,
        Instant deliveredAt,
        String status,
        String trackingUrl,
        Instant createdAt,
        Instant updatedAt
) {}
