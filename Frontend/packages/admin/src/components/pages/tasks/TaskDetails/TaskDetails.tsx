import { useMemo } from "react";
import Modal from "@core/components/ui/Modal/Modal";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useTasks } from "@core/lib/task/TasksContext";
import styles from "./TaskDetails.module.scss"

const TaskDetails = () => {

    const { user } = useAuth();
    const { selectedTask, selectTask, pickUpTask, completeTask } = useTasks();

    const isInProgress = selectedTask?.status?.code === "IN_PROGRESS";
    const isCanceled = selectedTask?.status?.code === "CANCELED";
    const isNew = selectedTask?.status?.code === "NEW";
    const isCompleted = Boolean(selectedTask?.completed);

    const canPickUp = isNew || isCanceled;
    const canComplete = user?.sub === selectedTask?.executor?.sub && isInProgress;
    const canCancel = canComplete || isNew;
    const canEdit = user?.sub === selectedTask?.executor?.sub && isInProgress;

    const formattedCreatedAt = useMemo(
        () => (selectedTask?.createdAt 
            ? new Date(selectedTask.createdAt).toLocaleString("ru-RU") : "-"),
        [selectedTask?.createdAt]
    );
    const formattedCompletedAt = useMemo(
        () => (selectedTask?.completed 
            ? new Date(selectedTask.completed).toLocaleString("ru-RU") : null),
        [selectedTask?.completed]
    );

    if (!selectedTask) return null;

    return (
        <Modal 
            isOpen={!!selectedTask} 
            onClose={() => selectTask(null)} 
            title={"Задача: " + selectedTask.name} 
            className={styles.tasksModal}
        >
            <div className={`${styles.details} ${selectedTask ? styles.active : ""}`}>
                <TaskDetail label="Название" value={selectedTask?.name} />
                <TaskDetail label="Описание" value={selectedTask?.description} />
                <TaskDetail label="Статус" value={selectedTask?.status?.name} />

                <br />

                <TaskDetail label="Автор" value={selectedTask?.author?.name} />

                {isCanceled && <TaskDetail label="Выполнил" value={selectedTask?.executor?.name ?? ""} />}
                {isInProgress && <TaskDetail label="Выполняющий" value={selectedTask?.executor?.name ?? ""} />}

                <br />

                <TaskDetail label="Создана" value={formattedCreatedAt} />

                {/* {!isCompleted && (
                    <TaskDetail
                        label="Дедлайн через"
                        value={<TimeCounter duration={new Date(task?.deadline).getTime() - new Date().getTime()} />}
                    />
                )} */}
                {isCompleted && <TaskDetail label="Завершена" value={formattedCompletedAt} />}

                <div className="button-actions">
                    {canPickUp && (
                        <ActionButton 
                            title="Взять"
                            type="primary" 
                            icon="download" 
                            taskId={selectedTask?.id} 
                            action={() => pickUpTask(selectedTask?.id)} 
                        />
                    )}
                    {canComplete && (
                        <ActionButton 
                            title="Завершить" 
                            type="confirm" 
                            icon="check" 
                            taskId={selectedTask?.id} 
                            action={() => completeTask(selectedTask?.id)} 
                        />
                    )}
                    {canCancel && (
                        <ActionButton 
                            title="Удалить" 
                            type="danger" 
                            icon="trash" 
                            taskId={selectedTask?.id} 
                        />
                    )}
                    {canEdit && (
                        <ActionButton 
                            title="Редактировать" 
                            type="primary" 
                            icon="edit" 
                            taskId={selectedTask?.id}
                        />
                    )}
                    <ActionButton 
                        title="Оставить комментарий" 
                        type="gold" 
                        icon="message" 
                        taskId={selectedTask?.id} 
                    />
                </div>
            </div>
        </Modal>
    );
};

const TaskDetail = ({ label, value}: {label: string, value: string | number | null}) => (
    <div className={styles.detail}>
        <h3 className={styles.title}>{label}:</h3>
        <span>{value || "-"}</span>
    </div>
);

const ActionButton = ({ 
    title, 
    type, 
    icon, 
    taskId, 
    action
}: {
    title: string, 
    type: string, 
    icon: string, 
    taskId: number, 
    action?: () => void
}) => (
    <button
        className={`btn btn-${type}`}
        data-task={taskId}
        onClick={action}
    >
        {title}
        <i className={`fa-solid fa-${icon}`}></i>
    </button>
);

export default TaskDetails;