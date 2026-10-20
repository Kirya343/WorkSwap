package org.workswap.statistic.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatisticRabbitConfig {

    @Bean
    public Queue onlineStatQueue() {
        return new Queue("onlineStatQueue", true);
    }

    @Bean
    public Queue usersStatQueue() {
        return new Queue("usersStatQueue", true);
    }

    @Bean
    public Queue listingViewQueue() {
        return new Queue("listingViewQueue", true);
    }

    @Bean
    public Queue siteViewQueue() {
        return new Queue("siteViewQueue", true);
    }

    @Bean
    public Queue listinsStatQueue() {
        return new Queue("listinsStatQueue", true);
    }
}
