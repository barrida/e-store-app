package com.estore.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPreferenceRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Channel is required")
        String channel,

        @NotBlank(message = "Template name is required")
        String templateName,

        Boolean isEnabled
) {}
