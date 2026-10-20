import { apiFetchJson } from '@core/lib/common/utils/apiClient';

export const statisticService = {
    getOnline: () => apiFetchJson('/stats/online'),
    getOnlineMetricsMonthly: () => apiFetchJson('/stats/online-metrics/month'),

    getUsersCountMetrics: (intervalType: string, multiplier: number) =>
        apiFetchJson(`/stats/users-count`, {}, {intervalType, multiplier}),
    getListingsCountMetrics: (intervalType: string, multiplier: number) =>
        apiFetchJson(`/stats/listings-count`, {}, {intervalType, multiplier}),
    getViewsCountMetrics: (intervalType: string, multiplier: number) =>
        apiFetchJson(`/stats/views-count`, {}, {intervalType, multiplier})
}