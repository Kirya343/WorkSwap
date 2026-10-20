package org.workswap.sso.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.workswap.rabbit.queues.events.UserCreatedEvent;
import org.workswap.sso.datasource.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProducer {

    private final RabbitTemplate template;

    public void userCreated(User user) {

        log.debug("Created user {} {} and published to Rabbit", user.getStatus().toString(), user.getId());

        UserCreatedEvent event = new UserCreatedEvent(
            user.getSub(), 
            user.getName(), 
            user.getEmail(), 
            user.getAvatarUrl(), 
            user.getStatus().toString()
        );

        template.convertAndSend("userCreateQueue", event);
    }
}
