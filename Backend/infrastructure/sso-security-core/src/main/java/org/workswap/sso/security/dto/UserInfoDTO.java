package org.workswap.sso.security.dto;

import org.workswap.sso.security.enums.UserStatus;

public record UserInfoDTO(
    String sub,
    String name,
    String email,
    String avatarUrl,
    UserStatus status
) {
}
