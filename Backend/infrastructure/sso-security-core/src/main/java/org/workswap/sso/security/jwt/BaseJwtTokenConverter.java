package org.workswap.sso.security.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseJwtTokenConverter implements JwtAuthenticationConverter {

    public AbstractAuthenticationToken convert(Jwt jwt) {
        
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        String userSub = jwt.getSubject();

        UserStatus userStatus = UserStatus.valueOf(Objects.requireNonNull(jwt.getClaim("status")));

        UserAuthData authData = new UserAuthData(userSub, userStatus);

        return new UserJwtAuthenticationToken(jwt, authorities, authData);
    }
}
