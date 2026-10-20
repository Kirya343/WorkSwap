package org.workswap.sso.security.service;

import java.io.IOException;
import java.lang.reflect.Method;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.workswap.sso.security.exceptions.ForbiddenException;
import org.workswap.sso.security.exceptions.UnauthenticatedException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("api")
@RequiredArgsConstructor 
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthorizationService authorizationService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        try {
            authorizationService.authorize(method, authentication);
        } catch (UnauthenticatedException e) {
            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED
            );
            
            return false;
        } catch (ForbiddenException e) {
            response.sendError(
                HttpServletResponse.SC_FORBIDDEN
            );

            return false;
        }

        return true;
    }

    @PostConstruct
    public void init() {
        log.debug("AuthenticationInterceptor registered");
    }
}
