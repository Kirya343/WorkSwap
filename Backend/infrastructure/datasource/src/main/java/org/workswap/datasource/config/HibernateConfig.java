package org.workswap.datasource.config;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.workswap.datasource.testers.QueryCounter;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer queryCounterCustomizer(
        QueryCounter queryCounter
    ) {
        return properties -> properties.put(
            "hibernate.session_factory.statement_inspector",
            queryCounter
        );
    }
}