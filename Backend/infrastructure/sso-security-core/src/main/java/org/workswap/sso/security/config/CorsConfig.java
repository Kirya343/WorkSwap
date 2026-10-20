package org.workswap.sso.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@ConfigurationProperties(prefix = "cors")
@Getter
public class CorsConfig {

    private List<String> domains = new ArrayList<>();
}