package com.estore.order.model;

public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static PaymentStatus fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
