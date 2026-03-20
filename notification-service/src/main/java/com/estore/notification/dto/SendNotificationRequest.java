package com.estore.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendNotificationRequest(

        String templateName,

        Long userId,

        @NotBlank(message = "Recipient address is required")
        String recipientAddress,

        @NotNull(message = "Channel is required")
        String channel,

        String subject,

        String body,

        String referenceType,

        Long referenceId
) {}
