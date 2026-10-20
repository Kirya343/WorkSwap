import { Interval } from "@core/lib/common/constants/intervalType";
import { statisticService } from "@core/lib/common/services/statisticService";
import { formatSignedValue } from "@core/lib/common/utilsService"
import { useEffect, useState } from "react";
import StatCard from "../StatCard/StatCard";

interface ListingMetrics {
    listingsCount: number;
    publishedListingsCount: number;
    temporaryListingsCount: number;
    listingsChange: number;
    publishedListingsChange: number;
    temporaryListingsChange: number;
}

const ListingsStatCard = ({interval}: {interval: Interval}) => {
    const [listingsCount, setListingsCount] = useState(0)
    const [metrics, setMetrics] = useState<ListingMetrics | null>(null);

    useEffect(() => {
        async function loadOnlineMetrics(interval: Interval) {
            const data = await statisticService.getListingsCountMetrics(interval.type, interval.multiplier);
            console.log(data)
            setMetrics(data);
            setListingsCount(data.publishedListingsCount);
        }

        loadOnlineMetrics(interval)
    }, [interval])

    const metricsText = `Всего объявлений: ${metrics?.listingsCount} 
        Активных объявлений: ${metrics?.publishedListingsCount}
        Черновиков объявлений: ${metrics?.temporaryListingsCount}

        Показатели: (${interval.title})
        Все объявления: ${formatSignedValue(metrics?.listingsChange || 0)}
        Активных объявлений: ${formatSignedValue(metrics?.publishedListingsChange || 0)}
        Черновиков объявлений: ${formatSignedValue(metrics?.temporaryListingsChange || 0)}
    `

    return metrics && (
        <StatCard 
            title={"Объявления"}
            value={listingsCount}
            change={metrics?.listingsChange}
            tooltip={metricsText}
            changePercent={metrics.listingsChange / metrics.listingsCount * 100}
            isPositive={metrics.publishedListingsChange > 0}
        />
    )
};

export default ListingsStatCard;