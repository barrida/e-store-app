package com.estore.order.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
    FAILED;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static OrderStatus fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
