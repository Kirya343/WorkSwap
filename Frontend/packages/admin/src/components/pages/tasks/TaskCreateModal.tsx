import { useState } from "react";
import Modal from "@core/components/ui/Modal/Modal";
import { useTasks } from "@core/lib/task/TasksContext";
import { taskTypes, TaskTypeValue } from "@core/lib/task/constants/taskTypes";
import { ITaskCreate } from "@core/lib/task/types";

const TaskCreateModal = () => {

    const [isOpen, setOpen] = useState<boolean>(false);

    const { createTask } = useTasks();

    const [name, setName] = useState<string>('');
    const [description, setDescription] = useState<string>('');
    const [type, setType] = useState<TaskTypeValue | ''>('');
    const [deadline, setDeadline] = useState<string>('');

    const createTaskL = async () => {
        console.log(type)
        if (!type) return;
        const newTask: ITaskCreate = {
            name,
            description,
            type,
            deadline
        }
        try {
            await createTask(newTask);   
        } catch {

        } finally {
            setOpen(false)
        }

    }

    return (
        <>
            <button className="btn btn-primary" onClick={() => setOpen(true)}>+ Создать задачу</button>
            <Modal
                isOpen={isOpen}
                onClose={() => setOpen(false)}
                title="Добавить задачу"
                id="tasksModal"
            >
                <div className="form-group">
                    <label htmlFor="taskName">Название:</label>
                    <input 
                        type="text" 
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="taskDescription">Описание:</label>
                    <span>(как можно подробнее)</span>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        rows={2}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="taskType">Тип задания:</label>
                    <select
                        name="taskType"
                        id="taskType"
                        className="form-control"
                        required
                        value={type} // <-- связываем с состоянием
                        onChange={e => setType(e.target.value as TaskTypeValue)}
                    >
                        <option value="">Тип задания</option>
                        {taskTypes.map(t => (
                            <option key={t.code} value={t.code}>
                                {t.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="deadline">Дедлайн:</label>
                    <input
                        type="datetime-local"
                        className="form-control"
                        value={deadline}
                        onChange={(e) => setDeadline(e.target.value)}
                    />
                </div>

                <div className="form-actions">
                    <button id="saveTaskBtn" className="btn btn-outline-primary" onClick={createTaskL}>
                        Сохранить
                    </button>
                </div>
            </Modal>
        </>
    );
};

export default TaskCreateModal;