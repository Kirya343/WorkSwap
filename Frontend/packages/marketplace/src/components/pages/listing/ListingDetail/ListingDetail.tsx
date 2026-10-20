import { ReactNode } from "react";
import styles from "./ListingDetail.module.scss"

export default function ListingDetail({ children, title, customValue }: { children: ReactNode, title: string, customValue?: boolean }) {
    return (
        <div className={styles.detail}>
            <span className={styles.label}>{title}:</span>
            {customValue ? <span className={styles.value}>{children}</span> : children}
        </div>
    )
}