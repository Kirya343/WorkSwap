package org.workswap.task.services;

import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.task.dto.TaskCreateDTO;
import org.workswap.task.dto.TaskDTO;

public interface TaskCommandService {
    
    TaskDTO createTask(
        UserAuthData authData, 
        TaskCreateDTO dto
    );
    void createComment(
        UserAuthData authData, 
        Long taskId, 
        String commentContent
    );
    void deleteComment(UserAuthData authData, Long commentId);
    void cancelTask(Long taskId);
    void pickupTask(UserAuthData authData, Long taskId);
    void completeTask(UserAuthData authData, Long taskId);
}
