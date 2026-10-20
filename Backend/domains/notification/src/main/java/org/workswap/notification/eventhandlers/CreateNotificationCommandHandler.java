package org.workswap.notification.eventhandlers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.workswap.notification.services.NotificationCommandService;
import org.workswap.shared.events.notification.CreateNotificationCommand;

import lombok.RequiredArgsConstructor;

@Component
@Profile("server")
@RequiredArgsConstructor
public class CreateNotificationCommandHandler {

    private final NotificationCommandService notificationCommandService;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CreateNotificationCommand event) {
        notificationCommandService.sendNotification(event);
    }
}
