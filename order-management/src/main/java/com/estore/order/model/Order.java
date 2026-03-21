package com.estore.order.model;

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
@Table(schema = "order_management", value = "orders")
public class Order {

    @Id
    private Long id;

    private String orderNumber;
    private Long userId;

    /** Stored as lowercase string matching PostgreSQL order_status enum. */
    private String status;

    /** Stored as lowercase string matching PostgreSQL payment_status enum. */
    private String paymentStatus;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;

    /** JSONB column stored as JSON string. */
    private String shippingAddress;

    /** JSONB column stored as JSON string. */
    private String billingAddress;

    private String couponCode;
    private BigDecimal couponDiscount;
    private String notes;

    /** INET column stored as String. */
    private String ipAddress;

    private String userAgent;
    private Instant placedAt;
    private Instant confirmedAt;
    private Instant shippedAt;
    private Instant deliveredAt;
    private Instant cancelledAt;
    private Instant createdAt;
    private Instant updatedAt;
}
