import ListingsGrid from "@/components/ui/listings/ListingsGrid/ListingsGrid";
import { ReactNode } from "react";
import styles from "./AccountListingsGrid.module.scss"

export default function AccountListingsGrid({children, fallback}: {children?: ReactNode, fallback?: ReactNode}) {
    return (
        <ListingsGrid className={styles.grid} fallback={fallback}>
            {children}
        </ListingsGrid>
    )
}