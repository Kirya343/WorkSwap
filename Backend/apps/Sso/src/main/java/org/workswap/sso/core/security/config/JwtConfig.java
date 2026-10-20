package org.workswap.sso.core.security.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties
@RequiredArgsConstructor
public class JwtConfig {
    
    @Value("${jwt.private-key-location:file:./ssl/jwt-private.pem}")
    private String privateKeyLocation;

    @Value("${jwt.public-key-location:file:./ssl/jwt-public.pem}")
    private String publicKeyLocation;

    private final ResourceLoader resourceLoader;
    
    @Bean
    public RSAKey rsaKey() throws Exception {
        // Загружаем приватный ключ
        RSAPrivateKey privateKey = loadPrivateKey();
        
        // Загружаем публичный ключ  
        RSAPublicKey publicKey = loadPublicKey();
        
        // Создаем RSAKey с обоими ключами
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        // Загружаем публичный ключ для валидации JWT
        RSAPublicKey publicKey = loadPublicKey();
        
        // Создаем JwtDecoder с публичным ключом
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
    
    private RSAPrivateKey loadPrivateKey() throws Exception {

        String privateKeyLoc = privateKeyLocation;
        if (privateKeyLoc == null) {
            throw new IllegalStateException("Private SSL key not found");
        }
        Resource resource = resourceLoader.getResource(privateKeyLoc);
        String privateKeyPEM = new String(resource.getInputStream().readAllBytes());

        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey() throws Exception {

        String publicKeyLoc = publicKeyLocation;
        if (publicKeyLoc == null) {
            throw new IllegalStateException("Public SSL key not found");
        }

        Resource resource = resourceLoader.getResource(publicKeyLoc);
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
}
