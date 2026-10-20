package org.workswap.notification.services;

import java.util.List;

import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.sso.security.dto.UserAuthData;

public interface NotificationQueryService {
    List<FullNotificationDTO> getUserNotifications(UserAuthData authData);
}
