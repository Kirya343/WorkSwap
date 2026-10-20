package org.workswap.datasource.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.workswap.datasource.logging.CustomHibernateConnectionLogger;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "org.workswap.chat",
        "org.workswap.forum",
        "org.workswap.listing",
        "org.workswap.location",
        "org.workswap.notification",
        "org.workswap.order",
        "org.workswap.review",
        "org.workswap.subscription",
        "org.workswap.task",
        "org.workswap.user",
        "org.workswap.permission"
    }, // Пакет основной БД
    entityManagerFactoryRef = "centralEntityManagerFactory",
    transactionManagerRef = "centralTransactionManager"
)
@Profile({"server", "statistic"})
@Slf4j
public class CentralDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.central-datasource")
    public DataSource centralDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean centralEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(centralDataSource())
                .packages(
                    "org.workswap.chat",
                    "org.workswap.forum",
                    "org.workswap.listing",
                    "org.workswap.location",
                    "org.workswap.notification",
                    "org.workswap.order",
                    "org.workswap.review",
                    "org.workswap.subscription",
                    "org.workswap.task",
                    "org.workswap.user",
                    "org.workswap.permission"
                )
                .persistenceUnit("central")
                .properties(Map.of(
                    "hibernate.connection.provider_class", CustomHibernateConnectionLogger.class.getName()
                ))
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager centralTransactionManager(
            @Qualifier("centralEntityManagerFactory") EntityManagerFactory emf
    ) {

        if (emf == null) {
            throw new IllegalArgumentException("EntityManagerFactory не найден (null)");
        }
        
        return new JpaTransactionManager(emf);
    }
}