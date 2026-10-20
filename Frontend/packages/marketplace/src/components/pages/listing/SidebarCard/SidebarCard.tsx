import { ReactNode } from "react"
import styles from "./SidebarCard.module.scss"

export default function SidebarCard({ children, className }: { children: ReactNode, className?: string }) {
    return(
        <div className={`${styles.card} ${className} fade-down`}>
            {children}
        </div>
    )
}