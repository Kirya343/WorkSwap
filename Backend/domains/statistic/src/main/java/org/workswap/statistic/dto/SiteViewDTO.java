package org.workswap.statistic.dto;

import java.time.LocalDateTime;

public record SiteViewDTO(
    String codeName,
    LocalDateTime timestamp
) {}
