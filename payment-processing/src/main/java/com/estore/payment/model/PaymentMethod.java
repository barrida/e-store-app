package com.estore.payment.model;

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
@Table(schema = "payment_processing", value = "payment_methods")
public class PaymentMethod {

    @Id
    private Long id;

    private Long userId;

    /** Stored as lowercase string matching PostgreSQL payment_method_type enum. */
    private String methodType;

    private Boolean isDefault;
    private String gateway;
    private String gatewayToken;
    private String gatewayCustomerId;
    private String displayName;
    private String cardLast4;
    private String cardBrand;
    private Short cardExpMonth;
    private Short cardExpYear;

    /** JSONB column stored as JSON string. */
    private String billingAddress;

    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
