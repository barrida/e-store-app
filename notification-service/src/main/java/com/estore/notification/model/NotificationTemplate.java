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
@Table(schema = "notification_service", value = "notification_templates")
public class NotificationTemplate {

    @Id
    private Long id;

    private String name;

    /** Stored as lowercase string matching PostgreSQL notification_channel enum. */
    private String channel;

    private String subject;
    private String bodyHtml;
    private String bodyText;

    /** JSONB column stored as JSON string. */
    private String variables;

    private Boolean isActive;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;
}
