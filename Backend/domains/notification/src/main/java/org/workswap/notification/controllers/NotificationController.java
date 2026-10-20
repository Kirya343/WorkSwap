package org.workswap.notification.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.services.NotificationCommandService;
import org.workswap.notification.services.NotificationQueryService;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    @GetMapping("/for-user")
    @RequiredPermission("GET_NOTIFICATIONS")
    public List<FullNotificationDTO> getNotification(
            @AuthUser UserAuthData authData
    ) {
        return notificationQueryService.getUserNotifications(authData);
    }

    @PatchMapping("/{notificationId}/read")
    @RequiredPermission("READ_NOTIFICATION")
    public void markAsReadNotification(
            @PathVariable Long notificationId, 
            @AuthUser UserAuthData authData
    ) {
        notificationCommandService.markAsRead(authData, notificationId);
    }
}
