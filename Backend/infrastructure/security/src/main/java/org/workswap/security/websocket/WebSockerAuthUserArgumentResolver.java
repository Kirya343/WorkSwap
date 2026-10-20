package org.workswap.security.websocket;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.annotations.parameters.OptionalAuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.jwt.UserJwtAuthenticationToken;

@Component
@Profile("server")
public class WebSockerAuthUserArgumentResolver
        implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class)
                || parameter.hasParameterAnnotation(OptionalAuthUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            Message<?> message
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        UserAuthData user = null;

        if (authentication instanceof UserJwtAuthenticationToken token) {
            user = (UserAuthData) token.getPrincipal();
        }

        if (parameter.hasParameterAnnotation(OptionalAuthUser.class)) {
            return Optional.ofNullable(user);
        }

        return user;
    }
}