"use client"

import { useEffect, useState } from 'react';
import ForumTopicCreateModal from '../../../components/pages/forum/ForumTopicCreateModal/ForumTopicCreateModal';
import { ForumTag, IForumTopic } from '@core/lib/forum/types';
import { forumService } from '@core/lib/forum/forumService';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import ForumTopicCard from '../../../components/pages/forum/ForumTopicCard/ForumTopicCard';
import styles from "./ForumPage.module.scss"

export default function ForumPage() {
    
    const [tags, setTags] = useState<ForumTag[] | null>(null);
    const [forumTopics, setForumTopics] = useState<IForumTopic[] | null>(null);

    const { dict } = useI18n()

    useEffect(() => {
        async function loadRecentTopics(count: number, translationsFilter: boolean) {
            const data = await forumService.getRecentTopics(count, translationsFilter);
            setForumTopics(data);
        }
        async function loadTags() {
            const data = await forumService.getTags();
            setTags(data);
        }

        loadTags();
        loadRecentTopics(20, false);
    }, [])

    return (
        <>
            <div className={styles.header}>
                <h1>{dict.navigation.forum}</h1>
                <ForumTopicCreateModal tags={tags}/>
            </div>
            <div className={styles.topicList}>
                <h3>{dict.common.forum.popularTopics}</h3>
                {forumTopics?.slice()
                    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                    .map((topic: IForumTopic) => (
                        <ForumTopicCard key={topic.openId} topic={topic} />
                    ))
                }
            </div>
        </>
    );
}