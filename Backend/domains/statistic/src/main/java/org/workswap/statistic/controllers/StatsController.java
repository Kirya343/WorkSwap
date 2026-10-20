package org.workswap.statistic.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.statistic.dto.ListingsStatsMetricDTO;
import org.workswap.statistic.dto.OnlineStatsMetricsDTO;
import org.workswap.statistic.dto.UsersStatsMetricDTO;
import org.workswap.statistic.dto.ViewsStatsMetricDTO;
import org.workswap.statistic.enums.IntervalType;
import org.workswap.statistic.enums.StatSaveIntervalType;
import org.workswap.statistic.services.StatisticQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stats")
@Profile("server")
@RequiredArgsConstructor
public class StatsController {

    private final StatisticQueryService statisticQueryService;

    @GetMapping("/views")
    @RequiredPermission("VIEW_LISTING_STATS")
    public List<Map<String, Object>> getViewsStats(
            @RequestParam Long listingId,
            @RequestParam StatSaveIntervalType interval,
            @RequestParam(required = false, defaultValue = "7") int days
    ) {
        return statisticQueryService.getListingStatGrath(listingId, interval, days);
    }

    @GetMapping("/online-metrics/month")
    @RequiredPermission("GET_ONLINE_METRICS")
    public OnlineStatsMetricsDTO getMonthlyMetrics() {
        return statisticQueryService.getMonthlyMetrics();
    }

    @GetMapping("/online")
    @RequiredPermission("GET_ONLINE")
    public Integer getOnline() {
        return statisticQueryService.getLastOnlineSnapshot();
    }

    @GetMapping("/users-count")
    @RequiredPermission("GET_ONLINE")
    public UsersStatsMetricDTO getUsersCount(
        @RequestParam String intervalType,
        @RequestParam int multiplier
    ) {
        return statisticQueryService.getUsersCountMetrics(IntervalType.valueOf(intervalType), multiplier);
    }

    @GetMapping("/listings-count")
    @RequiredPermission("GET_ONLINE")
    public ListingsStatsMetricDTO getListingsCount(
        @RequestParam String intervalType,
        @RequestParam int multiplier
    ) {
        return statisticQueryService.getListingsCountMetrics(IntervalType.valueOf(intervalType), multiplier);
    }

    @GetMapping("/views-count")
    @RequiredPermission("GET_ONLINE")
    public ViewsStatsMetricDTO getViewsCount(
        @RequestParam String intervalType,
        @RequestParam int multiplier
    ) {
        return statisticQueryService.getViewsCountMetrics(IntervalType.valueOf(intervalType), multiplier);
    }
}