package org.workswap.statistic.amqp.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.SiteViewDTO;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("statistic")
@RequiredArgsConstructor
public class SiteViewConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "siteViewQueue")
    public void siteViewQueue(SiteViewDTO dto) {
        
        statisticCommandService.saveSiteView(dto);
    }
}
