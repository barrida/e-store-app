package com.estore.notification.dto;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long templateId,
        String channel,
        String status,
        Long userId,
        String recipientAddress,
        String subject,
        String body,
        String referenceType,
        Long referenceId,
        String provider,
        String providerMessageId,
        String providerResponse,
        String errorMessage,
        Short retryCount,
        Instant nextRetryAt,
        Instant queuedAt,
        Instant sentAt,
        Instant deliveredAt,
        Instant failedAt,
        Instant createdAt,
        Instant updatedAt
) {}
