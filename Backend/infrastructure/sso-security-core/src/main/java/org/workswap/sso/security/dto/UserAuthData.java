package org.workswap.sso.security.dto;

import java.util.Objects;

import org.workswap.sso.security.enums.UserStatus;

public record UserAuthData(
    String sub,
    UserStatus status
) {
    public UserAuthData {
        Objects.requireNonNull(sub, "sub must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }
}