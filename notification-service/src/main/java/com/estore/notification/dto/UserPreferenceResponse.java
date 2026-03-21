package com.estore.notification.dto;

import java.time.Instant;

public record UserPreferenceResponse(
        Long id,
        Long userId,
        String channel,
        String templateName,
        Boolean isEnabled,
        Instant createdAt,
        Instant updatedAt
) {}
