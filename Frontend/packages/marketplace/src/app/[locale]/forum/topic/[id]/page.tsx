"use client"

import Avatar from "@core/components/common/Avatar/Avatar";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { IForumPost, IForumTopic } from "@core/lib/forum/types";
import { IShortUser } from "@core/lib/user/types";
import { redirect, useParams } from "next/navigation";
import { useEffect, useState } from "react";
import ForumPost from "../../../../../components/pages/forum/ForumPost/ForumPost";
import { forumService } from "@core/lib/forum/forumService"
import ActionMenu from "@core/components/ui/ActionMenu/ActionMenu";
import FormattedDateToNow from "@core/components/common/date/FormattedDateToNow"
import styles from "./ForumTopicPage.module.scss";
import ForumTextInputForm from "@/components/pages/forum/ForumTextInputForm/ForumTextInputForm";

const ForumTopicPage = () => {

    const { dict } = useI18n()

    const { user } = useAuth();
    const [topic, setTopic] = useState<IForumTopic| null>(null);
    const { id } = useParams();
    const topicOpenId = String(id)
    const [newPostTxt, setNewPostTxt] = useState('');
    const [sending, setSending] = useState(false);

    const createPost = async(content: string) => {
        setSending(true);
        const author: IShortUser = {
            name: user?.name ?? "",
            avatarUrl: user?.avatarUrl ?? "",
            sub: user?.sub ?? ""
        }
        const newPost: IForumPost = {
            topicOpenId: topicOpenId ?? "",
            openId: "", 
            content,
            createdAt: new Date().toISOString(), 
            author: author,
            comments: []
        };
        const data = await forumService.createPost(newPost);

        setSending(false);

        if (!data) throw new Error("Ошибка создания поста");

        setNewPostTxt('');

        const savedPost: IForumPost = {
            ...newPost,
            openId: data
        };

        setTopic(prev => {
            if (!prev) return prev;

            return {
                ...prev,
                posts: [...(prev.posts ?? []), savedPost] // добавляем новый пост в конец
            };
        });
    }

    useEffect(() => {

        async function loadTopicTheme(topicOpenId: string) {
            const data: IForumTopic = await forumService.getTopic(topicOpenId);
            setTopic(data);
        }

        if (topicOpenId) {
            loadTopicTheme(topicOpenId);
        }
    }, [topicOpenId])

    const actions = [];
    if (user?.sub == topic?.author.sub) {
        actions.push({
            title: "Изменить",
            func: () => null,
            icon: "pen"
        })
        actions.push({
            title: "Удалить",
            func: async () => {
                const confirmed = window.confirm("Вы уверены в том хотите удалить это обсуждение? Это действие необратимо!");
                if (confirmed && topic?.openId) {
                    const res = await forumService.deleteTopic(topic?.openId);
                    if (res.ok) {
                        redirect("/forum");
                    }
                }
            },
            icon: "trash"
        })
    }

    return topic && (
            <>
                <div className={styles.topic}>
                    <Avatar user={topic.author} size={50} />
                    <div className={styles.body}>
                        <div className={styles.header}>
                            <span className={styles.authorName}>{topic.author.name}</span>
                            <div className={styles.actions}>
                                <FormattedDateToNow date={topic.createdAt} />
                                <ActionMenu actions={actions} />
                            </div>
                        </div>
                        <h3 className={styles.title}>{topic.title}</h3>
                        <span className={styles.content}>{topic.content}</span>
                        {topic.tagName && (
                            <div className={styles.forumTag}>{dict.forumtags[topic.tagName]}</div>
                        )}
                    </div>
                </div>
                <div className={styles.postList}>
                    <ForumTextInputForm
                        placeholder='Напишите ответ...'
                        onFormSend={(content) => createPost(content)} 
                        disabled={sending}
                        addAvatar
                        className={styles.postForm}
                    />
                    {topic.posts?.slice()
                        .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
                        .map((post) => (
                        <ForumPost key={post.openId} post={post} setTopic={setTopic} />
                    ))}
                </div>
            </>
        );
}

export default ForumTopicPage;