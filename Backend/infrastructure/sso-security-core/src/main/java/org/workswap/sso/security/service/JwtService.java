package org.workswap.sso.security.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.jwk.RSAKey;

import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("api")
public class JwtService {

    private final RSAKey rsaKey;

    /**
     * Проверяет токен, валидирует подпись и срок жизни
     * @param token строка JWT
     * @return claims (JWTClaimsSet), если токен валиден, иначе null
     */
    public JWTClaimsSet validate(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Проверяем подпись по публичному ключу
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
            if (!signedJWT.verify(verifier)) {
                return null; // подпись не сошлась
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Проверяем срок жизни
            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                return null; // токен просрочен
            }

            return claims;

        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    public Jwt parseToSpringJwt(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);

        RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
        if (!signedJWT.verify(verifier)) {
            throw new SecurityException("JWT signature validation failed");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        Date expiration = claims.getExpirationTime();
        if (expiration == null || expiration.before(new Date())) {
            throw new SecurityException("JWT is expired");
        }

        // Переносим claims в Map
        Map<String, Object> claimsMap = claims.getClaims();

        // Создаём Jwt (Spring Security объект)
        return new Jwt(
                token,
                claims.getIssueTime() != null ? claims.getIssueTime().toInstant() : Instant.now(),
                claims.getExpirationTime() != null ? claims.getExpirationTime().toInstant() : Instant.now().plusSeconds(60),
                Map.of("alg", signedJWT.getHeader().getAlgorithm().getName()),
                claimsMap
        );
    }

    public String validateAndGetUserSub(String token) {
        JWTClaimsSet claims = validate(token);
        return (claims != null) ? claims.getSubject() : null;
    }
}