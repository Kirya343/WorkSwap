package org.workswap.notification.controllers;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.services.NotificationQueryService;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final NotificationQueryService notificationQueryService;
 
    @MessageMapping("/notifications.loadNotifications")
    @SendToUser("/queue/notifications/history.notifications")
    @Authenticated
    public List<FullNotificationDTO> loadMessagesForChat(
        @AuthUser UserAuthData authData
    ) {
        return notificationQueryService.getUserNotifications(authData);
    }
}
