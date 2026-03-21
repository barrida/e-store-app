package com.estore.notification.model;

public enum NotificationStatus {
    QUEUED,
    SENDING,
    SENT,
    DELIVERED,
    FAILED,
    BOUNCED;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static NotificationStatus fromDbValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
