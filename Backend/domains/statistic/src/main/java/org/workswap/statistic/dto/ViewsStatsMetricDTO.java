package org.workswap.statistic.dto;

public record ViewsStatsMetricDTO(
    int viewsCount,
    int standartsUsersViewsCount,
    int tempUsersViewsCount,

    int viewsChange,
    int standardUsersViewsChange,
    int tempUsersViewsChange
) {}
