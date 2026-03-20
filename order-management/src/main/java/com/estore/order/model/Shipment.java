package com.estore.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "order_management", value = "shipments")
public class Shipment {

    @Id
    private Long id;

    private Long orderId;
    private String trackingNumber;
    private String carrier;
    private String shippingMethod;
    private LocalDate estimatedDelivery;
    private Instant shippedAt;
    private Instant deliveredAt;
    private String status;
    private String trackingUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
