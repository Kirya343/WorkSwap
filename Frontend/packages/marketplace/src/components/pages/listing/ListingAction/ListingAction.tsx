import { ReactNode } from "react"
import styles from "./ListingAction.module.scss"
import { redirect } from "next/navigation"

interface ListingActionProps {
    children: ReactNode, 
    onClick?: () => void, 
    href?: string 
}

export default function ListingAction({ children, onClick, href}: ListingActionProps) {
    return(
        <button
            className={`${styles.action} hover`}
            onClick={(e) => {
                e.stopPropagation()
                if (onClick) { 
                    onClick()
                } else if (href) {
                    redirect(href)
                }
            }}
        >
            {children}
        </button>
    )
}