package com.estore.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentTransactionResponse(
        Long id,
        String transactionReference,
        Long orderId,
        Long userId,
        Long paymentMethodId,
        String paymentType,
        String status,
        BigDecimal amount,
        String currency,
        String gateway,
        String gatewayTransactionId,
        String gatewayResponse,
        Instant authorizedAt,
        Instant capturedAt,
        Instant failedAt,
        Instant refundedAt,
        String failureReason,
        BigDecimal refundedAmount,
        Long parentTransactionId,
        String ipAddress,
        String userAgent,
        String metadata,
        Instant createdAt,
        Instant updatedAt
) {}
