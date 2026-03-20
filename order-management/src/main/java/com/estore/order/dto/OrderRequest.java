package com.estore.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Subtotal is required")
        @DecimalMin(value = "0.00", message = "Subtotal must be non-negative")
        BigDecimal subtotal,

        BigDecimal discountAmount,

        BigDecimal shippingAmount,

        BigDecimal taxAmount,

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.00", message = "Total amount must be non-negative")
        BigDecimal totalAmount,

        String currency,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotBlank(message = "Billing address is required")
        String billingAddress,

        String couponCode,

        BigDecimal couponDiscount,

        String notes,

        String ipAddress,

        String userAgent,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemRequest> items
) {}
