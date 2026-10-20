import Breadcrumbs, { Breadcrumb } from "@core/components/ui/Breadcrumbs/Breadcrumbs"
import styles from "./ItemViewLayout.module.scss"
import { ReactNode } from "react"
import UserInfoSidebar from "@/components/pages/listing/UserInfoSidebar/UserInfoSidebar"
import { IShortUserProfile } from "@core/lib/user/types"
import ReviewsSection from "@/components/ui/reviews/ReviewsSection"

interface ItemViewLayoutProps {
    breadcrumbs?: Breadcrumb[]
    header?: ReactNode
    content?: ReactNode
    sidebarNode?: ReactNode,
    primarySidebarNode?: ReactNode,
    listingId?: number
    author?: IShortUserProfile
}

export default function ItemViewLayout({
    breadcrumbs, header, content, sidebarNode, primarySidebarNode, listingId, author
}: ItemViewLayoutProps) {

    return(
        <main className={styles.main}>
            {breadcrumbs && <Breadcrumbs crumbs={breadcrumbs}/>}
            
            {header && <div className={styles.header}>{header}</div>}

            <div className={styles.mainContent}>
                <div className={styles.content}>

                    {content}
                </div>

                <div className={styles.sidebar}>

                    {primarySidebarNode}

                    <UserInfoSidebar listingId={listingId ?? null} author={author ?? null} />

                    {sidebarNode}
                </div>
            </div>

            <ReviewsSection listingId={listingId ?? null} profileSub={author?.sub ?? null} />
        </main>
    )
}