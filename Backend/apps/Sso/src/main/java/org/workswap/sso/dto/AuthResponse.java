package org.workswap.sso.dto;

import java.util.Map;

public record AuthResponse(
    boolean success,
    String message,
    Map<String, Object> instructions
) {
}
