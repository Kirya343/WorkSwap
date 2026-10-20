package org.workswap.statistic.amqp.producers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.AllListingsStatSnapshotDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class ListingsStatProducer {

    private final RabbitTemplate template;

    public void sendListingsStat(AllListingsStatSnapshotDTO dto) {

        template.convertAndSend("listinsStatQueue", dto);
    }
}

