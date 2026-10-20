package org.workswap.notification.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.notification.datasource.model.Notification;
import org.workswap.notification.enums.NotificationType;
import org.workswap.shared.enums.Importance;

public record FullNotificationDTO(
    Long id,
    Long recipientId,
    boolean isRead,
    String title,
    String content,
    String link,
    NotificationType type,
    Importance importance,
    LocalDateTime createdAt
) {
    public static FullNotificationDTO ofNotification(Notification notification) {
        
        return new FullNotificationDTO(
            notification.getId(), 
            notification.getRecipient().getId(), 
            notification.isRead(), 
            notification.getTitle(),
            notification.getContent(), 
            notification.getLink(), 
            notification.getType(), 
            notification.getImportance(), 
            notification.getCreatedAt()
        );
    }

    public static List<FullNotificationDTO> ofList(Collection<Notification> notifications) {
        return notifications.stream().map(n -> FullNotificationDTO.ofNotification(n)).toList();
    }
}
