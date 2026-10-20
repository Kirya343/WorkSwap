package org.workswap.security.service;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.sso.security.jwt.JwtAuthenticationConverter;
import org.workswap.sso.security.jwt.UserJwtAuthenticationToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CachedPermissionsJwtTokenConverter implements JwtAuthenticationConverter {

    private final PermissionsService permissionsService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        log.debug("JWT claims: {}", jwt.getClaims());
        log.debug("JWT subject: {}", jwt.getSubject());

        String userSub = Objects.requireNonNull(jwt.getSubject());

        log.debug("jwt parsed: {}", userSub);

        Collection<GrantedAuthority> authorities =
            permissionsService.getUserPermissions(userSub);

        UserAuthData authData = new UserAuthData(
            userSub,
            UserStatus.valueOf(jwt.getClaim("status"))
        );

        return new UserJwtAuthenticationToken(jwt, authorities, authData);
    }
}
