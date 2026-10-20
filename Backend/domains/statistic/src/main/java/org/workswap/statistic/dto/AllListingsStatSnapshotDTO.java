package org.workswap.statistic.dto;

import java.time.LocalDateTime;

public record AllListingsStatSnapshotDTO(
    int publichedListings,
    int tempListings,
    LocalDateTime timestamp
) {}