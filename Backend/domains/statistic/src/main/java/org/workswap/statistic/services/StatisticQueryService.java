package org.workswap.statistic.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.workswap.statistic.dto.ListingsStatsMetricDTO;
import org.workswap.statistic.dto.OnlineStatsMetricsDTO;
import org.workswap.statistic.dto.UsersStatsMetricDTO;
import org.workswap.statistic.dto.ViewsStatsMetricDTO;
import org.workswap.statistic.enums.IntervalType;
import org.workswap.statistic.enums.StatSaveIntervalType;

public interface StatisticQueryService {
    
    long getTotalViews(Long userId);

    Map<String, Object> getUserStats(Long userId, Locale locale);

    int getMonthlyListingStats(Long listingId, int daysBack, String metric);

    int getLastOnlineSnapshot();

    OnlineStatsMetricsDTO getMonthlyMetrics();
    UsersStatsMetricDTO getUsersCountMetrics(IntervalType intervalType, int multiplier);
    ListingsStatsMetricDTO getListingsCountMetrics(IntervalType intervalType, int multiplier);
    ViewsStatsMetricDTO getViewsCountMetrics(IntervalType intervalType, int multiplier);

    List<Map<String, Object>> getListingStatGrath(Long listingId, StatSaveIntervalType interval, int days);
}
