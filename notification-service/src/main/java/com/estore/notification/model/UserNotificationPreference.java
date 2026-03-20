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
@Table(schema = "notification_service", value = "user_notification_preferences")
public class UserNotificationPreference {

    @Id
    private Long id;

    private Long userId;

    /** Stored as lowercase string matching PostgreSQL notification_channel enum. */
    private String channel;

    private String templateName;
    private Boolean isEnabled;
    private Instant createdAt;
    private Instant updatedAt;
}
