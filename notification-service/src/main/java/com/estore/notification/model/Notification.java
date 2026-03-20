package com.estore.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "notification_service", value = "notifications")
public class Notification {

    @Id
    private Long id;

    private Long templateId;

    /** Stored as lowercase string matching PostgreSQL notification_channel enum. */
    private String channel;

    /** Stored as lowercase string matching PostgreSQL notification_status enum. */
    private String status;

    private Long userId;
    private String recipientAddress;
    private String subject;
    private String body;
    private String referenceType;
    private Long referenceId;
    private String provider;
    private String providerMessageId;

    /** JSONB column stored as JSON string. */
    private String providerResponse;

    private String errorMessage;
    private Short retryCount;
    private Instant nextRetryAt;
    private Instant queuedAt;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant failedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
