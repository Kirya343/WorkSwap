import { TaskTypeValue } from "@core/lib/task/constants/taskTypes";
import { IStatus } from "../common/types/status";
import { IShortUser } from "@core/lib/user/types";

export interface ITask {
    id: number;
    name: string;
    description: string;

    status: IStatus;

    type: TaskTypeValue;

    author: IShortUser;
    executor: IShortUser | null;

    createdAt: string;
    deadline: string;
    completed: string | null;
}

export interface IUserTasks {
    executing: ITask[];
    completedLastMonth: ITask[];
    completedBefore: number;
}

export interface ITaskCreate {
    name: string;
    description: string;
    type: TaskTypeValue;
    deadline: string;
}

export interface ITaskComment {
    id: number,
    taskId: number,
    content: string,
    createdAt: string,
    author: IShortUser
}