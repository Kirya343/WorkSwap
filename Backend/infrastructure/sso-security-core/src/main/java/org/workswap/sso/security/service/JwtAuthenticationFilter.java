package org.workswap.sso.security.service;

import java.io.IOException;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.workswap.sso.security.jwt.JwtAuthenticationConverter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("api")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtTokenConverter;
    private final CookieBearerTokenResolver bearerTokenResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = bearerTokenResolver.resolve(request);

        log.debug("Request: {}", request.getRequestURI());
        log.debug("token: {}", token);

        if (token != null) {
            try {
                Jwt jwt = jwtDecoder.decode(token);

                AbstractAuthenticationToken authentication =
                    jwtTokenConverter.convert(jwt);

                SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            } catch (JwtException | AuthenticationException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    @PostConstruct
    public void init() {
        log.debug(
            "Registered JwtAuthenticationConverter: {}",
            jwtTokenConverter.getClass().getName()
        );
    }
}