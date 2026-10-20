package org.workswap.notification.enums;

public enum NotificationType{
    SYSTEM("Системное"),
    CHAT("Чат");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
