package org.workswap.sso.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
@Profile("api")
@RequiredArgsConstructor
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private static final String COOKIE_NAME = "accessToken";

    @Override
    public String resolve(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            return null; // позволяем заголовку иметь приоритет
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
