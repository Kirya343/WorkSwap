import { formatSignedValue } from "@core/lib/common/utilsService"
import { statisticService } from "@core/lib/common/services/statisticService";
import { useEffect, useState } from "react";
import { Interval } from "@core/lib/common/constants/intervalType";
import StatCard from "../StatCard/StatCard";

interface IViewsMetrics {
    viewsCount: number;
    standartsUsersViewsCount: number;
    tempUsersViewsCount: number;
    viewsChange: number;
    standardUsersViewsChange: number;
    tempUsersViewsChange: number;
}

const ViewsStatCard = ({interval}: {interval: Interval}) => {
    const [viewsCount, setViewsCount] = useState(0)
    const [metrics, setMetrics] = useState<IViewsMetrics | null>(null);

    useEffect(() => {
        async function loadOnlineMetrics(interval: Interval) {
            const data = await statisticService.getViewsCountMetrics(interval.type, interval.multiplier);
            console.log(data)
            setMetrics(data);
            setViewsCount(data.viewsCount);
        }

        loadOnlineMetrics(interval)
    }, [interval])

    const metricsText = `Всего просмотров: ${metrics?.viewsCount} 
        Просмотры от обычных пользователей: ${metrics?.standartsUsersViewsCount}
        Просмотры от временных пользователей: ${metrics?.tempUsersViewsCount}

        Показатели: (${interval.title})
        Все просмотры: ${formatSignedValue(metrics?.viewsChange || 0)}
        Просмотры от обычных пользователей: ${formatSignedValue(metrics?.standardUsersViewsChange || 0)}
        Просмотры от временных пользователей: ${formatSignedValue(metrics?.tempUsersViewsChange || 0)}
    `

    return metrics && (
        <StatCard
            title={"Просмотры"}
            value={viewsCount}
            change={metrics?.viewsChange}
            tooltip={metricsText}
            changePercent={metrics.viewsChange / metrics.viewsCount * 100}
            isPositive={metrics.viewsChange > 0}
        />
    )
};

export default ViewsStatCard;