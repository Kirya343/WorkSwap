import { useEffect, useState } from "react";
import { ITaskComment } from "@core/lib/task/types";
import { useAuth } from "@core/lib/auth/AuthContext";
import { taskService } from "@core/lib/task/tasksService"
import Avatar from "@core/components/common/Avatar/Avatar";
import styles from "./TaskComments.module.scss"

const TaskComments = ({taskId}: {taskId: number}) => {
    
    const { user } = useAuth();
    const [comments, setComments] = useState<ITaskComment[] | null>(null);

    useEffect(() => {
        if (!taskId) return;

        async function loadTaskComments() {
            try {
                const data = await taskService.getTaskComments(taskId);
                console.log(data);
                setComments(data || {});
            } catch (err) {
                console.error(err);
            }
        }
        
        loadTaskComments();
    }, [taskId]);

    useEffect(() => {
        document.querySelectorAll(".task-comment-card").forEach(comment => {
            comment.classList.add("active");
        });
    })

    return (
        <div className={styles.comments}>
            {comments?.map((comment) => (
                <div key={comment.id} className={`${styles.card} ${styles.active}`}>
                    <div className={styles.comment}>
                        <div className="flex-column">
                            <div className="flex-row" style={{ gap: "0.5rem" }}>
                                {/* Аватар */}
                                <Avatar 
                                    user={comment.author}        // передаём объект пользователя
                                    size={32}          // размер аватара, например 40px
                                />
                                <span style={{ margin: "auto 0" }}>
                                    {comment.author?.name}
                                </span>
                            </div>
                            <br />
                            <span>{comment.content}</span>
                        </div>

                        {user?.sub === comment.author?.sub && (
                            <div className={`button-actions flex-column ${styles.actions}`}>
                                <button
                                    data-comment={comment.id}
                                    data-task={taskId}
                                    className="btn btn-danger"
                                    style={{ height: "auto", right: 0 }}
                                >
                                    <i className="fa-solid fa-trash"></i>
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            ))}
        </div>
    );
};

export default TaskComments;