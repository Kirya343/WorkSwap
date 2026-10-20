import styles from "./Breadcrumbs.module.scss"
import Link from "next/link";

export interface Breadcrumb {
    href: string;
    title: string
}

export default function Breadcrumbs({crumbs}: {crumbs: Breadcrumb[]}) {
    return(
        <nav className={styles.breadcrumbs}>
            {crumbs.map((crumb, index) => (
                <div key={crumb.href} className={styles.crumb}>
                    <Link href={crumb.href} className={styles.link}>
                        {crumb.title}
                    </Link>

                    {index < crumbs.length - 1 && (
                        <span className={styles.divider}>/</span>
                    )}
                </div>
            ))}
        </nav>
    )
}