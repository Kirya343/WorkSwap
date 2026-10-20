"use client"

import { ITask, ITaskCreate } from "@core/lib/task/types";
import { createContext, useContext } from "react";

interface TasksContextValue {
    selectedTask: ITask | null;
    selectTask: (task: ITask | null) => void;

    pickUpTask: (taskId: number) => Promise<void>;
    completeTask: (taskId: number) => Promise<void>;
    createTask: (newTask: ITaskCreate) => Promise<void>;
}

export const TasksContext = createContext<TasksContextValue | null>(null);

export const useTasks = () => {
    const ctx = useContext(TasksContext);
    if (!ctx) {
        throw new Error("useTasks must be used within TasksContext.Provider");
    }
    return ctx;
};