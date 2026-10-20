package org.workswap.statistic.amqp.producers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.OnlineStatSnapshotDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class OnlineStatProducer {

    private final RabbitTemplate template;

    public void sendOnlineStat(OnlineStatSnapshotDTO dto) {

        template.convertAndSend("onlineStatQueue", dto);
    }
}

