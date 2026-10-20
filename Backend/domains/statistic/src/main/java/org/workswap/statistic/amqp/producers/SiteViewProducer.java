package org.workswap.statistic.amqp.producers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.SiteViewDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class SiteViewProducer {

    private final RabbitTemplate template;

    public void sendSiteView(SiteViewDTO dto) {

        template.convertAndSend("siteViewQueue", dto);
    }
}

