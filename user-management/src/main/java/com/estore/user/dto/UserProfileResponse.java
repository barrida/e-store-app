package com.estore.user.dto;

import java.time.Instant;
import java.time.LocalDate;

public record UserProfileResponse(
        Long userId,
        String firstName,
        String lastName,
        String displayName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        boolean phoneVerified,
        String avatarUrl,
        String bio,
        String locale,
        String timezone,
        Instant createdAt,
        Instant updatedAt
) {}
