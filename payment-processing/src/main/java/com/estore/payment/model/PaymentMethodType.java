package com.estore.payment.model;

public enum PaymentMethodType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    APPLE_PAY,
    GOOGLE_PAY,
    BANK_TRANSFER,
    CRYPTOCURRENCY;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static PaymentMethodType fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
