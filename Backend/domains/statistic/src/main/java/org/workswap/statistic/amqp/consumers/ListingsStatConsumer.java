package org.workswap.statistic.amqp.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.AllListingsStatSnapshotDTO;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("statistic")
@RequiredArgsConstructor
public class ListingsStatConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "listinsStatQueue")
    public void listinsStatQueue(AllListingsStatSnapshotDTO dto) {
        
        statisticCommandService.saveListingsStatSnapshot(dto);
    }
}
