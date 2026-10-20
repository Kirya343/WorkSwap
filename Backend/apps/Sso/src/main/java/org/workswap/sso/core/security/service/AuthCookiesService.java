package org.workswap.sso.core.security.service;

import java.time.Duration;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.workswap.sso.core.security.jwt.JwtIssuer;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.security.dto.UserAuthData;

import com.nimbusds.jose.JOSEException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthCookiesService {

    private static final Logger logger = LoggerFactory.getLogger(AuthCookiesService.class);

    private final JwtIssuer jwtIssuer;
    private final UserAuthDataService authDataService;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @Value("${app.cookie.sameSite}")
    private String cookieSameSite;
    
    public void setAuthCookies(HttpServletResponse response, User user) throws ServletException {

        UserAuthData authData = authDataService.load(
            Objects.requireNonNull(user.getId())
        );

        String accessToken;
        String refreshToken;

        try {
            logger.debug("Генерируем токены для: {}", user.getName());
            accessToken = jwtIssuer.issueAccessToken(authData);
            refreshToken = jwtIssuer.issueRefreshToken(authData);
        } catch (JOSEException e) {
            throw new ServletException("Ошибка генерации JWT", e);
        }

        logger.debug("accessToken: {}", accessToken);
        logger.debug("refreshToken: {}", refreshToken);

        ResponseCookie refreshCookie = setTokenCookie("refreshToken", refreshToken, Duration.ofDays(30));
        ResponseCookie accessCookie = setTokenCookie("accessToken", accessToken, Duration.ofMinutes(15));

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    public void deleteAuthCookies(HttpServletResponse response) throws ServletException {

        ResponseCookie refreshCookie = setTokenCookie("refreshToken", "", Duration.ZERO);
        ResponseCookie accessCookie = setTokenCookie("accessToken", "", Duration.ZERO);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private ResponseCookie setTokenCookie(String name, String token, Duration duration) {

        if (duration == null) {
            throw new IllegalArgumentException("Duration for cookie maxAge не может быть null");
        }

        ResponseCookie cookie = ResponseCookie.from(name, token)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .sameSite(cookieSameSite)
            .domain(cookieDomain.isEmpty() ? null : cookieDomain)
            .maxAge(duration)
            .build();

        logger.debug("установлены куки: {}", cookie.toString());

        return cookie;
    }
}