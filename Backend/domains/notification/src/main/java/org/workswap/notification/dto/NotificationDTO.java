package org.workswap.notification.dto;

import org.workswap.notification.enums.NotificationType;
import org.workswap.shared.enums.Importance;

public record NotificationDTO(
    String title,
    String content,
    String link,
    NotificationType type,
    Importance importance
) {}