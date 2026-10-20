package org.workswap.task.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskCreateDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.dto.TasksPageRequest;
import org.workswap.task.enums.TaskStatus;
import org.workswap.task.enums.TaskType;
import org.workswap.task.services.TaskCommandService;
import org.workswap.task.services.TaskQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final TaskQueryService taskQueryService;
    private final TaskCommandService taskCommandService;

    @PostMapping("/create")
    @RequiredPermission("CREATE_TASK")
    public TaskDTO createTask(
        @RequestBody TaskCreateDTO dto,
        @AuthUser UserAuthData authData
    ) {
        return taskCommandService.createTask(authData, dto);
    }

    @PostMapping("/{taskId}/pickup")
    @RequiredPermission("PICKUP_TASK")
    public void pickupTask(
        @PathVariable Long taskId, 
        @AuthUser UserAuthData authData
    ) {
        taskCommandService.pickupTask(authData, taskId);
    }

    @PostMapping("/{taskId}/complete")
    @RequiredPermission("COMPLETE_TASK")
    public void completeTask(
        @PathVariable Long taskId, 
        @AuthUser UserAuthData authData
    ) {
        taskCommandService.completeTask(authData, taskId);
    }

    @PostMapping("/{taskId}/cancel")
    @RequiredPermission("CANCEL_TASK")
    public void cancelTask(@PathVariable Long taskId) {
        taskCommandService.cancelTask(taskId);
    }

    @PostMapping("/{taskId}/comment")
    @RequiredPermission("CREATE_TASK_COMMENT")
    public void commentToTask(
        @PathVariable Long taskId,
        @RequestParam String commentContent,
        @AuthUser UserAuthData authData
    ) {
        taskCommandService.createComment(authData, taskId, commentContent);
    }

    @PostMapping("/comment/delete")
    @RequiredPermission("DELETE_TASK_COMMENT")
    public void deleteCommentToTask(
        @RequestParam Long commentId, 
        @AuthUser UserAuthData authData
    ) {
        taskCommandService.deleteComment(authData, commentId);
    }

    @GetMapping("/metadata")
    @RequiredPermission("GET_TASK_METADATA")
    public ResponseEntity<?> getTaskSettings() {
        return ResponseEntity.ok(Map.of(
            "taskStatusList", TaskStatus.values(), 
            "taskTypeList", TaskType.values()));
    }

    @GetMapping("/get-tasks")
    @RequiredPermission("GET_TASKS")
    public TasksPageRequest getTasksPage(@AuthUser UserAuthData authData) {
        return taskQueryService.getTasksPage(authData);
    }

    @GetMapping("/{taskId}/details")
    @RequiredPermission("VIEW_TASK_DETAILS")
    public TaskDTO getTaskDetails(@PathVariable Long taskId) {
        return taskQueryService.getTaskDetails(taskId);
    }

    @GetMapping("/{taskId}/comments")
    @RequiredPermission("VIEW_TASK_COMMENTS")
    public List<TaskCommentDTO> getTaskComments(@PathVariable Long taskId) {
        return taskQueryService.getTaskComments(taskId);
    }
}