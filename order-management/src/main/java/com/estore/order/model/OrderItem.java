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
@Table(schema = "order_management", value = "order_items")
public class OrderItem {

    @Id
    private Long id;

    private Long orderId;
    private Long productId;
    private String productSku;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private String currency;

    /** JSONB column stored as JSON string, e.g. {"color": "Red", "size": "M"}. */
    private String productOptions;

    private Instant createdAt;
    private Instant updatedAt;
}
