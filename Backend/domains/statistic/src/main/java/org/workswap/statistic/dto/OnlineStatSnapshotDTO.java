package org.workswap.statistic.dto;

import java.time.LocalDateTime;

public record OnlineStatSnapshotDTO(
    int online,
    LocalDateTime timestamp
) {}