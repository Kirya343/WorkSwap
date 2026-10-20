package org.workswap.statistic.dto;

import java.time.LocalDate;

public record OnlineStatsMetricsDTO(
    int minOnline,
    int maxOnline,
    double avgOnline,
    double medianOnline,
    double p95Online,
    double stdDeviation,
    int totalUserHours,
    LocalDate peakDay,
    Integer peakHour
) {}
