import { useState } from "react";
import ForumComment from "../ForumComment/ForumComment";
import { IForumComment, IForumPost, IForumTopic } from "@core/lib/forum/types";
import { useAuth } from "@core/lib/auth/AuthContext";
import { IShortUser } from "@core/lib/user/types";
import { forumService } from "@core/lib/forum/forumService";
import Avatar from "@core/components/common/Avatar/Avatar";
import FormattedDateToNow from "@core/components/common/date/FormattedDateToNow";
import ActionMenu from "@core/components/ui/ActionMenu/ActionMenu";
import styles from "./ForumPost.module.scss"
import PaperPlaneIcon from "@core/components/common/icons/PaperPlaneIcon";
import Textarea from "@core/components/ui/primitives/Textarea/Textarea";
import clsx from "clsx";
import ForumTextInputForm from "../ForumTextInputForm/ForumTextInputForm";

const ForumPost = ({
        post, setTopic
    }: {
        post: IForumPost,
        setTopic: React.Dispatch<React.SetStateAction<IForumTopic | null>>
    }) => {

    const [newCommentTxt, setNewCommentTxt] = useState('');
    const [sending, setSending] = useState(false);
    const { user } = useAuth();

    const createComment = async(postOpenId: string) => {
        const author: IShortUser = {
            name: user?.name ?? "",
            avatarUrl: user?.avatarUrl ?? "",
            sub: user?.sub ?? ""
        }
        const newComment: IForumComment = {
            id: 0,
            content: newCommentTxt,
            targetOpenId: postOpenId,
            createdAt: new Date().toISOString(),
            author: author
        }
        setSending(true);
        const data = await forumService.createComment(newComment);
        if (data) {
            setNewCommentTxt('');
            const savedComment: IForumComment = {
                ...newComment,
                id: Number(data)
            };
            setTopic(prev => {
                if (!prev?.posts) return prev;

                return {
                    ...prev,
                    posts: prev.posts.map(post =>
                        post.openId === postOpenId
                            ? {
                                ...post,
                                comments: [...post.comments, savedComment]
                            }
                            : post
                    )
                };
            });
        }
        setSending(false);
    }

    const actions = [
        {
            title: "Изменить",
            func: () => null,
            icon: "pen",
            access: user?.sub == post.author.sub
        },
        {
            title: "Удалить",
            func: async () => {
                const confirmed = window.confirm("Вы уверены в том хотите удалить этот ответ? Это действие необратимо!");
                if (confirmed) {
                    const postOpenId = post.openId
                    const res = await forumService.deletePost(postOpenId);
                    if (res.ok) {
                        setTopic(prev => {
                            if (!prev?.posts) return prev;

                            return {
                                ...prev,
                                posts: prev.posts.filter(post => post.openId !== postOpenId)
                            };
                        });
                    }
                }
            },
            icon: "trash",
            access: user?.sub == post.author.sub
        }
    ];

    return (
        <article className={styles.card}>
            <section className={styles.post}>
                <Avatar user={post.author} size={40} />

                <div className={styles.body}>

                    <div className={styles.header}>
                        <span className={styles.authorName}>{post.author.name}</span>
                        <div className={styles.actions}>
                            <FormattedDateToNow date={post.createdAt} className={styles.date}/>
                            <ActionMenu actions={actions}/>
                        </div>
                    </div>

                    <span className={styles.content}>{post.content}</span>
                </div>

            </section>
            
            <section className={styles.commentList}>
                <ForumTextInputForm
                    placeholder='Введите комментарий...'
                    onFormSend={(content) => createComment(content)} 
                    disabled={sending} 
                />
                {post.comments
                    .slice()
                    .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
                    .map((comment) => (
                        <ForumComment key={comment.id} comment={comment} setTopic={setTopic} />
                    ))
                }
            </section>
        </article>
    )
}

export default ForumPost;