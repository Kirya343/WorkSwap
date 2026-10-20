package org.workswap.task.services.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.task.datasource.model.Task;
import org.workswap.task.datasource.model.TaskComment;
import org.workswap.task.datasource.repository.TaskCommentRepository;
import org.workswap.task.datasource.repository.TaskRepository;
import org.workswap.task.dto.TaskCreateDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.enums.TaskStatus;
import org.workswap.task.services.TaskCommandService;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskCommandServiceImpl implements TaskCommandService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    public TaskDTO createTask(
        UserAuthData authData, 
        TaskCreateDTO dto
    ) {

        User user = userRepository.findBySub(authData.sub()).orElseThrow();
        Task task = new Task(
            dto.name(), 
            dto.description(), 
            dto.deadline(), 
            dto.type(), 
            user
        );

        Task saved = taskRepository.save(task);
        return TaskDTO.ofTask(saved);
    }
    
    public void createComment(
        UserAuthData authData, 
        Long taskId, 
        String commentContent
    ) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        User user = userRepository.findBySub(authData.sub()).orElseThrow();
        TaskComment comment = new TaskComment(commentContent, user, task);
        taskCommentRepository.save(comment);
    }

    public void deleteComment(UserAuthData authData, Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("ID комментария не указано");
        }

        TaskComment comment = taskCommentRepository.findById(commentId).orElse(null);
        User user = userRepository.findBySub(authData.sub()).orElseThrow();
        
        if (!comment.getAuthorId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы можете удалять только свои комментарии!");
        }

        taskCommentRepository.delete(comment);
    }

    public void cancelTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(TaskStatus.CANCELED);
        taskRepository.save(task);
    }

    public void pickupTask(UserAuthData authData, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        User user = userRepository.findBySub(authData.sub()).orElseThrow();
        task.setExecutor(user);
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskRepository.save(task);
    }

    public void completeTask(UserAuthData authData, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        User user = userRepository.findBySub(authData.sub()).orElseThrow();
        if (user.getId() != task.getExecutorId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы можете завершать только свои задачи!");
        }
        
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompleted(LocalDateTime.now());

        taskRepository.save(task);
    }
}
