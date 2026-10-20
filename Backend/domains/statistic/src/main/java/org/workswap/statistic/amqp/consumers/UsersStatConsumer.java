package org.workswap.statistic.amqp.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.UsersStatSnapshotDTO;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("statistic")
@RequiredArgsConstructor
public class UsersStatConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "usersStatQueue")
    public void usersStatQueue(UsersStatSnapshotDTO dto) {
        
        statisticCommandService.saveUsersStatSnapshot(dto);
    }
}
