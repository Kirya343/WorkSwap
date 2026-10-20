import { statisticService } from "@core/lib/common/services/statisticService";
import { useEffect, useState } from "react";
import StatCard from "../StatCard/StatCard";

interface OnlineMetrics {
    minOnline: number;
    maxOnline: number;
    medianOnline: number;
    avgOnline: number;
    p95Online: number;
    stdDeviation: number;
    totalUserHours: number;
    peakDay: number;
    peakHour: number;
}


const OnlineStatCard = () => {
    const [online, setOnline] = useState(0)
    const [metrics, setMetrics] = useState<OnlineMetrics | null>(null);

    useEffect(() => {
        async function loadOnline() {
            const data = await statisticService.getOnline();
            setOnline(data)
        }

        function loop() {
            loadOnline();
            setTimeout(loop, 5000);
        }
        
        async function loadOnlineMetrics() {
            const data = await statisticService.getOnlineMetricsMonthly();
            setMetrics(data)
        }

        loop();
        loadOnlineMetrics()
    }, [])

    const metricsText = `Минимальный онлайн: ${metrics?.minOnline} 
        Максимальный онлайн: ${metrics?.maxOnline}
        Медиана(типичный онлайн): ${metrics?.medianOnline}
        Средний онлайн за месяц: ${metrics?.avgOnline?.toFixed(1)}
        (p95) Онлайн, который почти никогда не превышается: ${metrics?.p95Online}
        Стандартное отклонение: ${metrics?.stdDeviation?.toFixed(1)}

        Часы онлайна за месяц: ${metrics?.totalUserHours}

        Дата максимального онлайна: ${metrics?.peakDay} ${metrics?.peakHour}:00
    `

    return metrics && <StatCard value={online} title={"Онлайн"} tooltip={metricsText} />
};

export default OnlineStatCard;