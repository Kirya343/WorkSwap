package org.workswap.statistic.dto;

public record UsersStatsMetricDTO(
    int usersCount,
    int standartsUsersCount,
    int tempUsersCount,
    
    int usersChange,
    int standardUsersChange,
    int tempUsersChange
) {}
