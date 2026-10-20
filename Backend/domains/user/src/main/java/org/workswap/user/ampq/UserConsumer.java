package org.workswap.user.ampq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.rabbit.queues.events.UserCreatedEvent;
import org.workswap.user.services.UserCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("server")
@RequiredArgsConstructor
public class UserConsumer {

    private final UserCommandService userCommandService;

    @RabbitListener(queues = "userCreateQueue")
    public void userCreateQueue(UserCreatedEvent event) {
        log.debug("Creating user {} {} by command", event.status(), event.sub());

        userCommandService.createUser(event);
    }
}
