package org.workswap.task.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.workswap.shared.dto.Status;
import org.workswap.task.datasource.model.Task;
import org.workswap.user.dto.ShortUserDTO;

public record TaskDTO(
    Long id,
    String name,
    String description,

    Status status,
    String type,

    ShortUserDTO author,
    ShortUserDTO executor,

    LocalDateTime createdAt,
    LocalDateTime deadline,
    LocalDateTime completed
) {

    public static TaskDTO ofTask(Task task) {
        
        return new TaskDTO(
            task.getId(),
            task.getName(),
            task.getDescription(),
            Status.ofStatus(task.getStatus()),
            task.getTaskType().getDisplayName(),
            ShortUserDTO.ofUser(task.getAuthor()),
            ShortUserDTO.ofUser(task.getExecutor()),
            task.getCreatedAt(),
            task.getDeadline(),
            task.getCompleted()
        );
    }

    public static List<TaskDTO> ofList(Collection<Task> tasks) {
        return tasks.stream().map(task -> TaskDTO.ofTask(task)).toList();
    }
}
