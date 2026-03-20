package com.estore.payment.model;

public enum PaymentType {
    CHARGE,
    REFUND,
    CHARGEBACK,
    AUTHORIZATION;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static PaymentType fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
