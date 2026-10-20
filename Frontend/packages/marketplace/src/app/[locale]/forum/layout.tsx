"use client"

import UserMeta from "@core/components/common/UserMeta";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { forumService } from "@core/lib/forum/forumService";
import { IForumActivityItem } from "@core/lib/forum/types";
import Link from "next/link";
import { ReactNode, useEffect, useState } from "react";
import styles from "./ForumLayout.module.scss"

const ForumLayout = ({children}: {children: ReactNode}) => {

    const { dict } = useI18n()
    const [activityItems, setActivityItems] = useState<IForumActivityItem[] | null>(null);

    useEffect(() => {
        async function loadActivity() {
            const data = await forumService.getActivity();
            setActivityItems(data);
        }

        loadActivity()
    }, [])
    
    return (
        <div className={styles.layout}>
            <main className={styles.page}>
                {children}
            </main>

            <aside className={styles.sidebar}>
                <h2>{dict.common.forum.lastActivity}</h2>
                {activityItems?.slice()
                    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                    .map(item => (
                        <Link 
                            key={item.title} 
                            href={item.link} 
                            className={styles.activity}
                        >
                            <UserMeta user={item.author} height={35} />
                            <span className={styles.title}>{item.title}</span>
                        </Link>
                    ))
                }
            </aside>
        </div>
    );
} 

export default ForumLayout;