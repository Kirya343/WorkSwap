package org.workswap.notification.services.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.notification.datasource.model.Notification;
import org.workswap.notification.datasource.repository.NotificationRepository;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.services.NotificationQueryService;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
@RequiredArgsConstructor
@Profile("server")
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public List<FullNotificationDTO> getUserNotifications(UserAuthData authData) {
        log.debug("Отправляем уведомления пользователю с Id: {}", authData.sub());

        List<Notification> notifications = notificationRepository.findByRecipientSub(authData.sub());

        return FullNotificationDTO.ofList(notifications);
    }
}
