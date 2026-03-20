package com.estore.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentTransactionRequest(

        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "User ID is required")
        Long userId,

        Long paymentMethodId,

        String paymentType,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        String currency,

        @NotBlank(message = "Gateway is required")
        String gateway,

        String ipAddress,
        String userAgent,
        String metadata
) {}
