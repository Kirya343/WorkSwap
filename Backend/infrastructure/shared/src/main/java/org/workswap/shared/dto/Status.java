package org.workswap.shared.dto;

import org.workswap.shared.enums.DisplayableEnum;

public record Status(
    String name, 
    String code
) {
    public static <T extends Enum<T> & DisplayableEnum> Status ofStatus(T status) {
        return new Status(
            status.getDisplayName(),
            status.name()
        );
    }
}
