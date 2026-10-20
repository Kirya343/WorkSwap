package org.workswap.sso.dto;

public record UserDeviceDTO(
    String fingerprint,
    String userAgent,
    String ip
) {}