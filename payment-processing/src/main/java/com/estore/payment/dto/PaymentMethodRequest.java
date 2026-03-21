package com.estore.payment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaymentMethodRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Method type is required")
        String methodType,

        @NotBlank(message = "Gateway is required")
        String gateway,

        @NotBlank(message = "Gateway token is required")
        String gatewayToken,

        String gatewayCustomerId,
        String displayName,

        @Size(min = 4, max = 4, message = "Card last4 must be exactly 4 digits")
        String cardLast4,

        String cardBrand,

        @Min(value = 1, message = "Card expiry month must be between 1 and 12")
        @Max(value = 12, message = "Card expiry month must be between 1 and 12")
        Short cardExpMonth,

        @Min(value = 2024, message = "Card expiry year must be 2024 or later")
        Short cardExpYear,

        String billingAddress,
        Boolean isDefault
) {}
