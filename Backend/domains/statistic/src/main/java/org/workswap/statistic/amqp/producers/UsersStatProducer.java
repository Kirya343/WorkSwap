package org.workswap.statistic.amqp.producers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.UsersStatSnapshotDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class UsersStatProducer {

    private final RabbitTemplate template;

    public void sendUsersStat(UsersStatSnapshotDTO dto) {

        template.convertAndSend("usersStatQueue", dto);
    }
}

