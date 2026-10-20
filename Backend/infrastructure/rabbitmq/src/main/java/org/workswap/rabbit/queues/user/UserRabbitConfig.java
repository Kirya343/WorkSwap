package org.workswap.rabbit.queues.user;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class UserRabbitConfig {

    @Bean
    public Queue userCreateQueue() {
        
        return new Queue("userCreateQueue", true);
    }
}
