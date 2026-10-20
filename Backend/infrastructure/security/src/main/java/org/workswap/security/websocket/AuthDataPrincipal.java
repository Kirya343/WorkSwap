package org.workswap.security.websocket;

import java.security.Principal;

import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthDataPrincipal implements Principal {

    private final UserAuthData authData;

    @Override
    public String getName() {
        // имя пользователя для Spring /user/queue/…
        return authData.sub();  
    }

    public UserAuthData getAuthData() {
        return authData;
    }
}
