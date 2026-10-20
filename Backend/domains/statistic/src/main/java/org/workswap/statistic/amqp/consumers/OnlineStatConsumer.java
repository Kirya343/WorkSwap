package org.workswap.statistic.amqp.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.OnlineStatSnapshotDTO;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("statistic")
@RequiredArgsConstructor
public class OnlineStatConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "onlineStatQueue")
    public void onlineStatQueue(OnlineStatSnapshotDTO dto) {
        
        statisticCommandService.saveOnlineStatSnapshot(dto);
    }
}
