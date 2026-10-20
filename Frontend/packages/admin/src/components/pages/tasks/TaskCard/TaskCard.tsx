import { ITask } from "@core/lib/task/types"
import { useTasks } from "@core/lib/task/TasksContext"
import styles from "./TaskCard.module.scss"

const TaskCard = ({task}: {task: ITask}) => {

    const { selectTask } = useTasks();

    return (
        <div className={styles.card} onClick={() => selectTask(task)}>
            <span className={styles.title}>{task.name}</span>

            <div className={styles.meta}>
                <span className={styles.taskType}>{task.type}</span>
                <span className={`${styles.status} ${styles[`status-${task.status.code.toLocaleLowerCase()}`]}`}>{task.status.name}</span>
            </div>
        </div>
    );
}

export default TaskCard;