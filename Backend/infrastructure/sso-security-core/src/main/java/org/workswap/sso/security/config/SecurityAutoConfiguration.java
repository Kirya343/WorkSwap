package org.workswap.sso.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.workswap.sso.security.jwt.BaseJwtTokenConverter;
import org.workswap.sso.security.jwt.JwtAuthenticationConverter;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@AutoConfiguration
@Slf4j
@Import(SecurityWebMvcConfig.class)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new BaseJwtTokenConverter();
    }

    @PostConstruct
    public void init() {
        log.debug("securityAutoConfiguration is loaded");
    }
}