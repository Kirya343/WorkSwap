import Avatar from "@core/components/common/Avatar/Avatar";
import FormattedDateToNow from "@core/components/common/date/FormattedDateToNow";
import ActionMenu from "@core/components/ui/ActionMenu/ActionMenu";
import { useAuth } from "@core/lib/auth/AuthContext";
import { forumService } from "@core/lib/forum/forumService";
import { IForumComment, IForumTopic } from "@core/lib/forum/types";
import styles from "./ForumComment.module.scss"

const ForumComment = ({
    comment, setTopic
}: {
    comment: IForumComment,
    setTopic: React.Dispatch<React.SetStateAction<IForumTopic | null>>
}) => {

    const { user } = useAuth();

    const actions = [
        {
            title: "Изменить",
            func: () => null,
            icon: "pen",
            access: user?.sub == comment?.author.sub
        },
        {
            title: "Удалить",
            func: async () => {
                const confirmed = window.confirm("Вы уверены в том хотите удалить этот комментарий? Это действие необратимо!");
                if (confirmed) {
                    const commentId = comment.id
                    const res = await forumService.deleteComment(commentId);
                    if (res.ok) {
                        setTopic(prev => {
                            if (!prev?.posts) return prev;

                            return {
                                ...prev,
                                posts: prev.posts.map(post => ({
                                    ...post,
                                    comments: post.comments.filter(comment => comment.id !== commentId)
                                }))
                            };
                        });
                    }
                }
            },
            icon: "trash",
            access: user?.sub == comment?.author.sub
        }
    ];

    return comment && (
        <article className={styles.card}>
            <Avatar user={comment.author} size={40} />

            <div className={styles.body}>

                <div className={styles.header}>
                    <span className={styles.authorName}>{comment.author.name}</span>
                    <div className={styles.actions}>
                        <FormattedDateToNow date={comment.createdAt} className={styles.date}/>
                        <ActionMenu actions={actions}/>
                    </div>
                </div>

                <span className={styles.content}>{comment.content}</span>
            </div>
        </article>
    )
}

export default ForumComment;