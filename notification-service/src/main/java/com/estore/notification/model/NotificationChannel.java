package com.estore.notification.model;

public enum NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
    IN_APP,
    WEBHOOK;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static NotificationChannel fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
