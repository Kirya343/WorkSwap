import { ReactNode } from "react"
import styles from "./ListingPageElement.module.scss"

export default function ListingPageElement({ title, children }: { title?: string, children: ReactNode }) {
    return(
        <div className={`${styles.card} fade-down`}>
            {title && <h2>{title}</h2>}
            {children}
        </div>
    )
}