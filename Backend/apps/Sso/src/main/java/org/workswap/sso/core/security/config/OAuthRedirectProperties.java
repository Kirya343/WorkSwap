package org.workswap.sso.core.security.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "security.oauth")
@Getter
@Setter
public class OAuthRedirectProperties {

    private Set<String> allowedOrigins = Set.of();
}