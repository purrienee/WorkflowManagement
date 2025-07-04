package com.ArushyRaina.WorkflowManagement.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ArushyRaina.WorkflowManagement.DTO.TaskCreateRequest;
import com.ArushyRaina.WorkflowManagement.DTO.TaskResponse;
import com.ArushyRaina.WorkflowManagement.entities.TaskEntity;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.services.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')") // <-- THE FIX IS HERE
    public ResponseEntity<TaskResponse> assignTask(
            @Valid @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal Users currentUser) {
        TaskEntity createdTaskEntity = taskService.assignTask(
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getAssignedToId(),
                
                currentUser.getUserId()
        );
        return new ResponseEntity<>(new TaskResponse(createdTaskEntity), HttpStatus.CREATED);
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal Users currentUser) {
        List<TaskEntity> taskEntities = taskService.getTasksForUser(currentUser.getUserId());
        List<TaskResponse> responseDtos = taskEntities.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestBody String newStatus,
            @AuthenticationPrincipal Users currentUser) {
        String cleanStatus = newStatus.replaceAll("\"", "");
        TaskEntity updatedTaskEntity = taskService.updateTaskStatus(taskId, cleanStatus, currentUser.getUserId());
        return ResponseEntity.ok(new TaskResponse(updatedTaskEntity));
    }
    
    @GetMapping("/{taskId}")
    @PostAuthorize("returnObject.body.assignedTo.username == authentication.name or returnObject.body.assignedBy.username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Integer taskId) {
        TaskEntity taskEntity = taskService.getTaskById(taskId);
        return ResponseEntity.ok(new TaskResponse(taskEntity));
    }
}