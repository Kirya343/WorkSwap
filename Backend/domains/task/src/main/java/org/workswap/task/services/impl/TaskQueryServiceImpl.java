package org.workswap.task.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.task.datasource.model.Task;
import org.workswap.task.datasource.model.TaskComment;
import org.workswap.task.datasource.repository.TaskCommentRepository;
import org.workswap.task.datasource.repository.TaskRepository;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.dto.TasksPageRequest;
import org.workswap.task.dto.UserTasksTable;
import org.workswap.task.enums.TaskStatus;
import org.workswap.task.services.TaskQueryService;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService{
    
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final UserRepository userRepository;

    public TasksPageRequest getTasksPage(UserAuthData authData) {

        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        Pageable pageable = PageRequest.of(0, 15);
        Page<Task> newTasks = taskRepository.findPageWithUsersFiltered(TaskStatus.NEW, null, "created", pageable);
        List<Task> executing = taskRepository.findByExecutorIdAndStatus(user.getId(), TaskStatus.IN_PROGRESS);
        List<Task> completed = taskRepository.findCompletedAfter(user.getId(), LocalDateTime.now().minusMonths(1));
        long completedBefore = taskRepository.countCompletedBefore(user.getId(), LocalDateTime.now().minusMonths(1));

        UserTasksTable userTasks = new UserTasksTable(
            TaskDTO.ofList(executing), 
            TaskDTO.ofList(completed), 
            completedBefore
        );

        Page<TaskDTO> list = newTasks.map(t -> TaskDTO.ofTask(t));
        return new TasksPageRequest(userTasks, list);
    }

    public TaskDTO getTaskDetails(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        return TaskDTO.ofTask(task);
    }

    public List<TaskCommentDTO> getTaskComments(Long taskId) {
        List<TaskComment> comments = taskCommentRepository.findAllByTaskId(taskId);
        return TaskCommentDTO.ofList(comments);
    }
}
