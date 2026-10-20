package org.workswap.statistic.dto;

import java.time.LocalDateTime;

public record UsersStatSnapshotDTO(
    int standartsUsers,
    int tempUsers,
    LocalDateTime timestamp
) {}