package com.estore.user.model;

public enum AddressType {
    BILLING("billing"),
    SHIPPING("shipping"),
    BOTH("both");

    private final String value;

    AddressType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AddressType fromValue(String value) {
        for (AddressType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AddressType: " + value);
    }
}
