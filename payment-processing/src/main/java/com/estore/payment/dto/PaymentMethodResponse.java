package com.estore.payment.dto;

import java.time.Instant;

public record PaymentMethodResponse(
        Long id,
        Long userId,
        String methodType,
        Boolean isDefault,
        String gateway,
        String gatewayCustomerId,
        String displayName,
        String cardLast4,
        String cardBrand,
        Short cardExpMonth,
        Short cardExpYear,
        String billingAddress,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
