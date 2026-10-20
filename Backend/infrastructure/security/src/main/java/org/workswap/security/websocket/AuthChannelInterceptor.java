package org.workswap.security.websocket;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.workswap.security.service.CachedPermissionsJwtTokenConverter;
import org.workswap.shared.events.user.UserOnlineState;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.service.JwtService;

import lombok.RequiredArgsConstructor;

@Component
@Profile("server")
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);

    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final CachedPermissionsJwtTokenConverter jwtTokenConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            Principal user = accessor.getUser();
            if (user != null) {
                logger.debug("STOMP current user class: {}, name: {}", user.getClass(), user.getName());
            } else {
                logger.debug("STOMP user is null");
            }
        }

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (sessionAttributes == null) throw new MessagingException("Missing session attributes");
            
            String token = (String) sessionAttributes.get("accessToken");

            if (token == null) throw new MessagingException("Missing access token");

            try {
                Jwt jwt = jwtService.parseToSpringJwt(token); // твой метод, который валидирует токен

                if (jwt == null) throw new IllegalStateException("Ошибка парсинга JWT токена");

                AbstractAuthenticationToken auth = jwtTokenConverter.convert(jwt);

                UserAuthData authData = (UserAuthData) auth.getPrincipal();

                logger.debug("Авторизуем вебсокет, authData: {}", authData.toString());

                eventPublisher.publishEvent(new UserOnlineState(authData.sub(), true));

                accessor.setUser(auth);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

                logger.debug("STOMP user authenticated: {}", context);

            } catch (Exception e) {
                logger.error("STOMP token validation failed", e);
                throw new MessagingException(e.getMessage(), e);
            }
        }

        return message;
    }
    
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) accessor.getUser();
        if (auth != null && auth.getPrincipal() instanceof UserAuthData) {
            UserAuthData authData = (UserAuthData) auth.getPrincipal();
            eventPublisher.publishEvent(new UserOnlineState(authData.sub(), false));
        }
    }
}