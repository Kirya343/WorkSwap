package org.workswap.statistic.dto;

public record ListingsStatsMetricDTO(
    int listingsCount,
    int publishedListingsCount,
    int temporaryListingsCount,

    int listingsChange,
    int publishedListingsChange,
    int temporaryListingsChange
) {}
