package com.estore.user.dto;

import java.time.Instant;

public record AddressResponse(
        Long id,
        Long userId,
        String addressType,
        boolean isDefault,
        String label,
        String firstName,
        String lastName,
        String company,
        String addressLine1,
        String addressLine2,
        String city,
        String stateProvince,
        String postalCode,
        String countryCode,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {}
