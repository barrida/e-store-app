package com.estore.user.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        boolean emailVerified,
        String authProvider,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
