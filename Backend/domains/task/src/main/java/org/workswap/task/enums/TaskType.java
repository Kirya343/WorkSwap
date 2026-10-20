package org.workswap.task.enums;

public enum TaskType {
    DEVELOPMENT("Разработка(BE)"),
    CONTENT_UPDATE("Дополнение контента"),
    MODERATION("Модерация"),
    DESIGN("Разработка(FE)"),
    TESTING("Тестирование"),
    MAINTENANCE("Обслуживание"),
    SUPPORT("Поддержка");

    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
