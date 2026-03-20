package com.estore.notification.dto;

import java.time.Instant;

public record NotificationTemplateResponse(
        Long id,
        String name,
        String channel,
        String subject,
        String bodyHtml,
        String bodyText,
        String variables,
        Boolean isActive,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {}
