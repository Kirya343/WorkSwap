import styles from "./ListingsGrid.module.scss"
import { ReactNode } from "react"

interface ListingsGridProps {
    children?: ReactNode,
    fallback?: ReactNode
    className?: string
}

export default function ListingsGrid({children, fallback, className}: ListingsGridProps) {
    return (
        <div className={`${styles.grid} ${className ?? ""}`}>
            {children ?? fallback}
        </div>
    )
}