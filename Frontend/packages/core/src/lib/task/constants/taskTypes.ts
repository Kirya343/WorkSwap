export const TaskType = Object.freeze({
    DEVELOPMENT: "DEVELOPMENT",
    CONTENT_UPDATE: "CONTENT_UPDATE",
    MODERATION: "MODERATION",
    DESIGN: "DESIGN",
    TESTING: "TESTING",
    MAINTENANCE: "MAINTENANCE",
    SUPPORT: "SUPPORT"
} as const);

export type TaskTypeKey = keyof typeof TaskType;
export type TaskTypeValue = typeof TaskType[TaskTypeKey];

// соответствие displayName
export const TaskTypeNames: Record<TaskTypeValue, string> = {
    DEVELOPMENT: "Разработка(BE)",
    CONTENT_UPDATE: "Дополнение контента",
    MODERATION: "Модерация",
    DESIGN: "Разработка(FE)",
    TESTING: "Тестирование",
    MAINTENANCE: "Обслуживание",
    SUPPORT: "Поддержка"
};

// массив для dropdown / фильтров и т.д.
export const taskTypes: { code: TaskTypeValue; name: string }[] =
    Object.values(TaskType).map(code => ({ code, name: TaskTypeNames[code] }));