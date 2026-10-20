import { ReactNode } from "react";
import styles from "./SettingSection.module.scss"

interface SettingSectionProps {
    title: string;
    subtitle?: string;
    children?: ReactNode;
}

export default function SettingSection({title, subtitle, children}: SettingSectionProps) {
    return (
        <div className={styles.section}>
            <h3 className={styles.title}>{title}</h3>
            {subtitle && <p>{subtitle}</p>}
            <div className={styles.body}>
                {children}
            </div>
        </div>
    )
}