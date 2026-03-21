package com.estore.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "user_management", value = "users")
public class User {

    @Id
    private Long id;

    @Column("email")
    private String email;

    @Column("email_verified")
    private boolean emailVerified;

    @Column("email_verified_at")
    private Instant emailVerifiedAt;

    /** NULL for OAuth users. Store as-is; hash before persisting in production. */
    @Column("password_hash")
    private String passwordHash;

    /** Stored as String to avoid custom R2DBC enum converter complexity. Maps to auth_provider DB enum. */
    @Column("auth_provider")
    private String authProvider;

    @Column("provider_user_id")
    private String providerUserId;

    /** Stored as String to avoid custom R2DBC enum converter complexity. Maps to user_status DB enum. */
    @Column("status")
    private String status;

    @Column("failed_login_count")
    private Short failedLoginCount;

    @Column("locked_until")
    private Instant lockedUntil;

    @Column("last_login_at")
    private Instant lastLoginAt;

    /** INET in PostgreSQL; stored as String in Java. */
    @Column("last_login_ip")
    private String lastLoginIp;

    @Column("password_changed_at")
    private Instant passwordChangedAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
