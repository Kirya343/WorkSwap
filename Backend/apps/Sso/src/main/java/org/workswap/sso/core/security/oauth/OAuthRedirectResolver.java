package org.workswap.sso.core.security.oauth;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.workswap.sso.core.security.config.OAuthRedirectProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthRedirectResolver {

    private static final Logger logger = LoggerFactory.getLogger(OAuthRedirectResolver.class);
    private final OAuthRedirectProperties properties;

    public String resolve(String redirect) {

        logger.debug("redirect: {}", redirect);
        logger.debug("allowedOrigins: {}", properties.getAllowedOrigins());
        if (redirect == null) {
            return properties.getAllowedOrigins().iterator().next();
        }

        try {
            URI uri = URI.create(redirect);
            String origin = uri.getScheme() + "://" + uri.getHost();

            if (uri.getPort() != -1) {
                origin += ":" + uri.getPort();
            }

            return properties.getAllowedOrigins().contains(origin)
                ? origin
                : properties.getAllowedOrigins().iterator().next();

        } catch (Exception e) {
            return properties.getAllowedOrigins().iterator().next();
        }
    }
}