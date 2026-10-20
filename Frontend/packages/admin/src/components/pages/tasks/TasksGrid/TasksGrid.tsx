
import { IUserTasks } from "@core/lib/task/types";
import TaskCard from "../TaskCard/TaskCard";
import styles from "./TasksGrid.module.scss"

const TasksGrid = ({tasks}: {tasks: IUserTasks | null}) => {
    
    return (
        <div className={styles.container}>
            <div className={styles.column}>
                <div className={styles.header}>В процессе</div>
                {tasks?.executing.map(task => (
                    <TaskCard key={task.id} task={task}/>
                ))}
            </div>
            <div className={styles.column}>
                <div className={styles.header}>Выполненные за месяц</div>
                {tasks?.completedLastMonth.map(task => (
                    <TaskCard key={task.id} task={task}/>
                ))}
                <div className={styles.footer}>Выполненые ранее: {tasks?.completedBefore}</div>
            </div>
        </div>
    )
}

export default TasksGrid;