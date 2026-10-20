package org.workswap.security.config.jwt;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jose.jwk.RSAKey;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties
@Profile("server")
@RequiredArgsConstructor
@Slf4j
public class JwtVerifierConfig {

    @Value("${jwt.public-key-location:file:./ssl/jwt-public.pem}")
    private String publicKeyLocation;

    private final ResourceLoader resourceLoader;

    @Bean
    public RSAKey rsaKey() throws Exception {
        RSAPublicKey publicKey = loadPublicKey();
        return new RSAKey.Builder(publicKey)
                .keyID("platform-public")
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        RSAPublicKey publicKey = loadPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private RSAPublicKey loadPublicKey() throws Exception {
        Resource resource = resourceLoader.getResource(Objects.requireNonNull(publicKeyLocation));
        String publicKeyPEM = new String(resource.getInputStream().readAllBytes());
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) factory.generatePublic(spec);
    }

    @PostConstruct
    public void init() {
        log.debug("JwtVerifierConfig CREATED");
    }
}