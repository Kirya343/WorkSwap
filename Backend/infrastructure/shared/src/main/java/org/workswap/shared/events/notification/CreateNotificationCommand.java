package org.workswap.shared.events.notification;

import org.workswap.shared.enums.Importance;

public record CreateNotificationCommand(
    String title,
    String content,
    String link,
    Long userId,
    Importance importance
) {
}