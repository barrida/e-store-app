package com.estore.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "payment_processing", value = "payment_transactions")
public class PaymentTransaction {

    @Id
    private Long id;

    private String transactionReference;
    private Long orderId;
    private Long userId;
    private Long paymentMethodId;

    /** Stored as lowercase string matching PostgreSQL payment_type enum. */
    private String paymentType;

    /** Stored as lowercase string matching PostgreSQL payment_status enum. */
    private String status;

    private BigDecimal amount;
    private String currency;
    private String gateway;
    private String gatewayTransactionId;

    /** JSONB column stored as JSON string. */
    private String gatewayResponse;

    private Instant authorizedAt;
    private Instant capturedAt;
    private Instant failedAt;
    private Instant refundedAt;
    private String failureReason;
    private BigDecimal refundedAmount;
    private Long parentTransactionId;

    /** INET column stored as String. */
    private String ipAddress;

    private String userAgent;

    /** JSONB column stored as JSON string. */
    private String metadata;

    private Instant createdAt;
    private Instant updatedAt;
}
