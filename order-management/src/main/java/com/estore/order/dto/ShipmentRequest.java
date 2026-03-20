package com.estore.order.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ShipmentRequest(

        @NotNull(message = "Order ID is required")
        Long orderId,

        String trackingNumber,

        String carrier,

        String shippingMethod,

        LocalDate estimatedDelivery,

        String trackingUrl
) {}
