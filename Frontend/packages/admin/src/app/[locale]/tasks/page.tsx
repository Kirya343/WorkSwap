"use client"

import TaskCreateModal from "../../../components/pages/tasks/TaskCreateModal";
import TasksAside from "../../../components/pages/tasks/TasksAside/TasksAside";
import TasksGrid from "../../../components/pages/tasks/TasksGrid/TasksGrid";
import { useEffect, useState } from "react";
import TaskDetails from "../../../components/pages/tasks/TaskDetails/TaskDetails";
import { ITask, ITaskCreate, IUserTasks } from "@core/lib/task/types";
import { taskService } from "@core/lib/task/tasksService";
import { useAuth } from "@core/lib/auth/AuthContext";
import { Page } from "@core/lib/common/types/page"
import { TasksContext } from "@core/lib/task/TasksContext";
import Card from "@/components/ui/Card/Card";
import styles from "./TasksPage.module.scss"

const TasksPage = () => {

    const [tasks, setTasks] = useState<IUserTasks | null>(null);
    const [newTasks, setNewTasks] = useState<Page<ITask> | null>(null);
    const [selectedTask, selectTask] = useState<ITask | null>(null);

    const { shortUser } = useAuth();
    
    useEffect(() => {
        async function loadSortedPage() {

            try {
                const data = await taskService.getTasksPage();
                console.log(data)
                setTasks(data.userTasks);
                setNewTasks(data.newTasks)
            } catch (err) {
                console.error(err);
            }
        };
    
        loadSortedPage();
    }, []);

    const pickUpTask = async (taskId: number): Promise<void> => {
        try {
            await taskService.pickUpTask(taskId);

            setNewTasks(prev => {
                if (!prev) return prev;
                const pickedTask = prev.content.find(t => t.id === taskId);
                if (!pickedTask) return prev;

                pickedTask.status = { code: "IN_PROGRESS", name: "В процессе" };

                setTasks(tasksPrev => {
                    if (!tasksPrev) return tasksPrev;
                    return { ...tasksPrev, executing: [...tasksPrev.executing, pickedTask] };
                });

                return { ...prev, content: prev.content.filter(t => t.id !== taskId) };
            });

        } catch (err) {
            console.error(err);
        }
    };

    const completeTask = async (taskId: number): Promise<void> => {
        try {
            await taskService.completeTask(taskId);

            setTasks(prev => {

                if (!prev) return prev;
                const completedTask = prev.executing.find(t => t.id === taskId);
                if (!completedTask) return prev;

                completedTask.status = { code: "COMPLETED", name: "Завершена" };

                shortUser && (
                    completedTask.author = shortUser
                )

                return { 
                    ...prev,
                    executing: (prev.executing.filter(t => t.id !== taskId)), 
                    completedLastMonth: [...prev.completedLastMonth, completedTask] 
                };
            });
        } catch (err) {
            console.error(err);
        }
    }

    const createTask = async (newTask: ITaskCreate): Promise<void> => {
        try {
            const data: ITask = await taskService.createTask(newTask);
            setNewTasks(prev => {
                if (!prev) return { 
                    content: [data], 
                    page: {
                        totalPages: 1, 
                        totalElements: 1,
                        number: 0,
                        size: 1,
                        first: false,
                        last: false
                    }
                };

                data.executor = shortUser;

                return {
                    ...prev,
                    content: [...prev.content, data]
                };
            });
        } catch (err) {
            console.error(err);
        }
    }

    return (
        <TasksContext.Provider value={{ 
            selectedTask, 
            selectTask, 
            pickUpTask,
            completeTask,
            createTask
        }}>
            <Card header={<TaskCreateModal />}>
                <div className={styles.page}>
                    <TasksGrid tasks={tasks}/>
                    <TasksAside tasks={newTasks}/>
                </div>
                <TaskDetails/>
            </Card>
        </TasksContext.Provider>
    );
};

export default TasksPage;