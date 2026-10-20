import Tooltip from "@core/components/common/Tooltip/Tooltip";
import { formatSignedValue } from "@core/lib/common/utilsService";
import styles from "./StatCard.module.scss"
import ArrowIcon from "@core/components/common/icons/ArrowIcon";

interface StatCardProps {
    value: string | number;
    change?: number; 
    title: string;
    tooltip: string;
    changePercent?: number;
    isPositive?: boolean;
}

export default function StatCard({ value, title, change, tooltip, changePercent, isPositive }: StatCardProps) {

    return (
        <div className={styles.card}>
            <div className={styles.title}>{title}</div>
            <Tooltip text={tooltip} title={title}>
                <div className={styles.valueContainer}>
                    <span className={styles.value}>{value}</span>
                    {change !== undefined && <span className={styles.change}>({formatSignedValue(change)})</span>}
                </div>
            </Tooltip>
            {changePercent !== undefined && 
                <div className={`${styles.changeContainer} ${isPositive ? styles.positive : styles.negative}`}>
                    {isPositive ? <ArrowIcon up /> : <ArrowIcon down />}
                    <span className={styles.changePercent}>{changePercent}% за месяц</span>
                </div>
            }
        </div>
    );
};