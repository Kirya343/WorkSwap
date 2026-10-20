package org.workswap.sso.security.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.workswap.sso.security.dto.UserAuthData;

public class UserJwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserAuthData authData;
    private final Jwt jwt;

    public UserJwtAuthenticationToken(Jwt jwt, Collection<GrantedAuthority> authorities, UserAuthData authData) {
        super(authorities);
        this.jwt = jwt;
        this.authData = authData;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt.getTokenValue();
    }

    @Override
    public Object getPrincipal() {
        return authData;
    }

    @Override
    public String getName() {
        return authData.sub();
    }

    public Jwt getJwt() {
        return jwt;
    }
}
