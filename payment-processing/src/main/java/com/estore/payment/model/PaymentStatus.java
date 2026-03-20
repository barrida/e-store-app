package com.estore.payment.model;

public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    DISPUTED;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static PaymentStatus fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
