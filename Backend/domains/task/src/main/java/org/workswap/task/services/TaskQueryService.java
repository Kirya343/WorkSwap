package org.workswap.task.services;

import java.util.List;

import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.dto.TasksPageRequest;

public interface TaskQueryService {

    TasksPageRequest getTasksPage(UserAuthData authData);
    TaskDTO getTaskDetails(Long taskId);
    List<TaskCommentDTO> getTaskComments(Long taskId);
}
