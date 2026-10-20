import UserMeta from "@core/components/common/UserMeta";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { IForumTopic } from "@core/lib/forum/types";
import Link from "next/link";
import { useEffect, useRef, useState } from "react";
import styles from "./ForumTopicCard.module.scss"
import MessagesIcon from "@core/components/common/icons/MessagesIcon";

const ForumTopicCard = ({topic}: {topic: IForumTopic}) => {

    const { dict } = useI18n()

    const titleRef = useRef<HTMLSpanElement>(null);
    const [isOpen, setOpen] = useState(false);
    const [isTruncated, setIsTruncated] = useState(false);

    useEffect(() => {
        const el = titleRef.current;
        if (!el) return;

        const check = () => {
            setIsTruncated(el.scrollWidth > el.clientWidth);
        };

        check();
        window.addEventListener("resize", check);
        return () => window.removeEventListener("resize", check);
    }, [topic.title]);

    return (
        <div className={styles.card}>
            <div className={styles.cardHeader}>
                <UserMeta user={topic.author} height={40} />
                <Link href={`/forum/topic/${topic.openId}`} className={styles.readBtn}>
                    <span>{dict.buttons.forum.readTopic}</span>
                    <div className='flex-row'>
                        {topic.postsCount}
                        <MessagesIcon size={24} />
                    </div>
                </Link>
            </div>

            <div className={styles.content}>
                <div className={styles.meta}>
                    <span 
                        ref={titleRef}
                        className={`${styles.topicTheme} ${isOpen ? styles.open : ""}`}
                    >
                        {topic.title}
                    </span>
                    {topic.tagName && (
                        <div className={styles.forumTag}>{dict.forumtags[topic.tagName]}</div>
                    )}
                </div>
                {isTruncated && (
                    <button
                        className={styles.collapseTheme}
                        onClick={() => setOpen(prev => !prev)}
                    >
                        {isOpen
                            ? dict.buttons.forum.collapse
                            : dict.buttons.forum.expand}
                    </button>
                )}
            </div>
        </div>
    );
}

export default ForumTopicCard;