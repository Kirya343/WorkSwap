package org.workswap.task.dto;

import org.springframework.data.domain.Page;

public record TasksPageRequest(

    UserTasksTable userTasks,

    Page<TaskDTO> newTasks
) {}
