import { ReactNode } from "react";
import styles from "./Card.module.scss"

export default function Card({header, children}: {header?: ReactNode, children: ReactNode}) {
    return (
        <div className={styles.card}>
            {header && 
                <div className={styles.header}>
                    {header}
                </div>
            }
            <div className={`${styles.body} normal-only`}>
                {children}
            </div>
        </div>
    )
}