package org.workswap.task.enums;

import org.workswap.shared.enums.DisplayableEnum;

public enum TaskStatus implements DisplayableEnum {
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершена"),
    CANCELED("Отменена");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}