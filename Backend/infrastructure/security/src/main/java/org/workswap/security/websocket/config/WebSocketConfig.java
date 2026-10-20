package org.workswap.security.websocket.config;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.workswap.security.websocket.AuthChannelInterceptor;
import org.workswap.security.websocket.AuthHandshakeInterceptor;
import org.workswap.security.websocket.WebSockerAuthUserArgumentResolver;

import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableWebSocketMessageBroker
@Profile("server")
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final WebSockerAuthUserArgumentResolver authUserArgumentResolver;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(authHandshakeInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public boolean configureMessageConverters(
            List<MessageConverter> messageConverters
    ) {
        JsonMapper objectMapper = JsonMapper.builder().build();

        JacksonJsonMessageConverter converter =
                new JacksonJsonMessageConverter(objectMapper);

        messageConverters.add(converter);

        return false;
    }

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(authUserArgumentResolver);
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration
    ) {
        registration.interceptors(
                authChannelInterceptor,
                new SecurityContextChannelInterceptor()
        );
    }
}