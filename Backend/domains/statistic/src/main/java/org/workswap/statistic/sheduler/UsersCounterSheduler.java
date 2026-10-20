package org.workswap.statistic.sheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.workswap.statistic.dto.UsersStatSnapshotDTO;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.statistic.amqp.producers.UsersStatProducer;
import org.workswap.user.datasource.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("server")
@Component
public class UsersCounterSheduler {
    
    private final UsersStatProducer usersStatProducer;
    private final UserRepository userRepository;

    @Value("${isTest}")
    private boolean isTest;

    @Scheduled(fixedRate = 15000)
    public void saveOnlineAnalytic() {

        if (isTest == false) {
            UsersStatSnapshotDTO dto = new UsersStatSnapshotDTO(
                Math.toIntExact(userRepository.countByStatus(UserStatus.ACTIVE)),
                Math.toIntExact(userRepository.countByStatus(UserStatus.TEMP)),
                LocalDateTime.now()
            );

            usersStatProducer.sendUsersStat(dto);
        }
    }
}
