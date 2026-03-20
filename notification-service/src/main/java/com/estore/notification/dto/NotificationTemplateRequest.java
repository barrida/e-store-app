package com.estore.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationTemplateRequest(

        @NotBlank(message = "Template name is required")
        String name,

        @NotNull(message = "Channel is required")
        String channel,

        String subject,

        String bodyHtml,

        @NotBlank(message = "Body text is required")
        String bodyText,

        String variables,

        Boolean isActive
) {}
