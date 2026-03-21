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
@Table(schema = "user_management", value = "addresses")
public class Address {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    /** Stored as String to avoid custom R2DBC enum converter complexity. Maps to address_type DB enum. */
    @Column("address_type")
    private String addressType;

    @Column("is_default")
    private boolean isDefault;

    @Column("label")
    private String label;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("company")
    private String company;

    @Column("address_line1")
    private String addressLine1;

    @Column("address_line2")
    private String addressLine2;

    @Column("city")
    private String city;

    @Column("state_province")
    private String stateProvince;

    @Column("postal_code")
    private String postalCode;

    /** ISO 3166-1 alpha-2 country code. */
    @Column("country_code")
    private String countryCode;

    @Column("phone_number")
    private String phoneNumber;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
