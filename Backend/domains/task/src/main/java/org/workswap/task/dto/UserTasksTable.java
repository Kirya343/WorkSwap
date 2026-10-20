package org.workswap.task.dto;

import java.util.List;

public record UserTasksTable(
    List<TaskDTO> executing,
    List<TaskDTO> completedLastMonth,
    long completedBefore
) {}
