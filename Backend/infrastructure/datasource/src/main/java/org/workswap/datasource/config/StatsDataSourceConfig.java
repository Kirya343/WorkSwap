package org.workswap.datasource.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.workswap.datasource.logging.CustomHibernateConnectionLogger;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "org.workswap.statistic",
    entityManagerFactoryRef = "statsEntityManagerFactory",
    transactionManagerRef = "statsTransactionManager"
)
@Profile({"server", "statistic"})
@Slf4j
public class StatsDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.statistics-datasource")
    public DataSource statsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean statsEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(statsDataSource())
                .packages("org.workswap.statistic") // Пакет с @Entity статистики
                .persistenceUnit("stats")
                .properties(Map.of(
                    "hibernate.connection.provider_class", CustomHibernateConnectionLogger.class.getName()
                ))
                .build();
    }

    @Bean
    public PlatformTransactionManager statsTransactionManager(
            @Qualifier("statsEntityManagerFactory") EntityManagerFactory emf
    ) {

        if (emf == null) {
            throw new IllegalArgumentException("EntityManagerFactory не найден (null)");
        }
        
        return new JpaTransactionManager(emf);
    }
}
