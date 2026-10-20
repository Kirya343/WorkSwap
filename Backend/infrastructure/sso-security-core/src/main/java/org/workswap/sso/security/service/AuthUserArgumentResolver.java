package org.workswap.sso.security.service;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.annotations.parameters.OptionalAuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.jwt.UserJwtAuthenticationToken;

@Component
@Profile("api")
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class)
                || parameter.hasParameterAnnotation(OptionalAuthUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
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