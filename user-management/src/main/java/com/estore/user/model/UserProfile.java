package com.estore.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "user_management", value = "user_profiles")
public class UserProfile {

    /** Also the FK to users(id). */
    @Id
    @Column("user_id")
    private Long userId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("display_name")
    private String displayName;

    @Column("date_of_birth")
    private LocalDate dateOfBirth;

    @Column("gender")
    private String gender;

    @Column("phone_number")
    private String phoneNumber;

    @Column("phone_verified")
    private boolean phoneVerified;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("bio")
    private String bio;

    @Column("locale")
    private String locale;

    @Column("timezone")
    private String timezone;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
