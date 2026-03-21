package com.estore.user.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserProfileRequest(

        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Size(max = 150, message = "Display name must not exceed 150 characters")
        String displayName,

        LocalDate dateOfBirth,

        @Size(max = 20, message = "Gender must not exceed 20 characters")
        String gender,

        @Size(max = 30, message = "Phone number must not exceed 30 characters")
        String phoneNumber,

        @Size(max = 2048, message = "Avatar URL must not exceed 2048 characters")
        String avatarUrl,

        String bio,

        @Size(max = 10, message = "Locale must not exceed 10 characters")
        String locale,

        @Size(max = 50, message = "Timezone must not exceed 50 characters")
        String timezone
) {}
