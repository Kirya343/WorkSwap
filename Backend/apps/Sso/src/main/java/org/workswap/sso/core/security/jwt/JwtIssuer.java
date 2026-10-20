package org.workswap.sso.core.security.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.workswap.sso.security.dto.UserAuthData;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
@RequiredArgsConstructor
public class JwtIssuer {
    
    private final RSAKey rsaKey;

    public String issueAccessToken(UserAuthData auth) throws JOSEException {

        Instant now = Instant.now();
        JWSSigner signer = new RSASSASigner(rsaKey);

        log.debug("auth {}", auth.sub());

        // Строим JWT
        JWTClaimsSet set = new JWTClaimsSet.Builder()
                .subject(auth.sub())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(Duration.ofMinutes(15))))
                .claim("status", auth.status())
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, set);
        jwt.sign(signer);
        return jwt.serialize();
    }

    public String issueRefreshToken(UserAuthData auth) throws JOSEException {

        Instant now = Instant.now();
        JWSSigner signer = new RSASSASigner(rsaKey);

        JWTClaimsSet set = new JWTClaimsSet.Builder()
                .subject(auth.sub())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(Duration.ofDays(30))))
                .claim("type", "refresh")
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, set);
        jwt.sign(signer);

        return jwt.serialize();
    }
}
