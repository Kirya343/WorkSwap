package org.workswap.security.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketAuthRulesConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        var builder = MessageMatcherDelegatingAuthorizationManager.builder();

        builder
            .simpDestMatchers("/app/**").authenticated()
            .simpSubscribeDestMatchers("/user/**").authenticated()
            .anyMessage().permitAll();

        return builder.build();
    }
}