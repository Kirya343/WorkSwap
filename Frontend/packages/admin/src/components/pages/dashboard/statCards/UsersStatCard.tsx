import { formatSignedValue } from "@core/lib/common/utilsService"
import { statisticService } from "@core/lib/common/services/statisticService";
import { useEffect, useState } from "react";
import StatCard from "../StatCard/StatCard";
import { Interval } from "@core/lib/common/constants/intervalType";

interface UserMetrics {
    usersCount: number;
    standartsUsersCount: number;
    tempUsersCount: number;
    usersChange: number;
    standardUsersChange: number;
    tempUsersChange: number;
}

const UsersStatCard = ({interval}: {interval: Interval}) => {
    const [usersCount, setUsersCount] = useState(0)
    const [metrics, setMetrics] = useState<UserMetrics | null>(null);

    useEffect(() => {
        async function loadOnlineMetrics(interval: Interval) {
            const data = await statisticService.getUsersCountMetrics(interval.type, interval.multiplier);
            console.log(data)
            setMetrics(data);
            setUsersCount(data.usersCount);
        }

        loadOnlineMetrics(interval)
    }, [interval])

    const metricsText = `Всего пользователей: ${metrics?.usersCount} 
        Зарегистрированных пользователей: ${metrics?.standartsUsersCount}
        Временных пользователей: ${metrics?.tempUsersCount}

        Показатели: (${interval.title})
        Все пользователи: ${formatSignedValue(metrics?.usersChange || 0)}
        Зарегистрированные пользователи: ${formatSignedValue(metrics?.standardUsersChange || 0)}
        Временные пользователи: ${formatSignedValue(metrics?.tempUsersChange || 0)}
    `

    return metrics && (
        <StatCard
            title={"Пользователи"}
            value={usersCount}
            change={metrics?.usersChange}
            tooltip={metricsText}
            changePercent={metrics.standardUsersChange / metrics.standartsUsersCount * 100}
            isPositive={metrics.standardUsersChange > 0}
        />
    )
};

export default UsersStatCard;