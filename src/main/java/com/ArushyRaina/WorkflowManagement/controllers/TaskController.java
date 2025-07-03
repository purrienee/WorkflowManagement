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
@RequestMapping("/api/tasks") // Base URL for all task-related endpoints
public class TaskController {

    private final TaskService taskService;

    // Inject the TaskService to handle all business logic
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates and assigns a new task.
     * This endpoint is protected and can only be accessed by users with the 'MANAGER' role.
     *
     * @param request The request body containing task details (title, description, etc.).
     * @param currentUser The currently authenticated user, provided by Spring Security.
     * @return A ResponseEntity containing the newly created task's safe data (TaskResponse DTO).
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TaskResponse> assignTask(
            @Valid @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal Users currentUser) {

        // Call the service to perform the business logic
        TaskEntity createdTaskEntity = taskService.assignTask(
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getAssignedToId(),
                currentUser.getUserId()
        );

        // Convert the internal database entity to a safe, public-facing DTO
        TaskResponse responseDto = new TaskResponse(createdTaskEntity);

        // Return the DTO with a 201 CREATED status
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves all tasks assigned to the currently logged-in user.
     *
     * @param currentUser The currently authenticated user.
     * @return A list of tasks assigned to that user.
     */
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal Users currentUser) {
        
        // Get the list of TaskEntity objects from the service
        List<TaskEntity> taskEntities = taskService.getTasksForUser(currentUser.getUserId());

        // Convert the list of entities to a list of safe DTOs
        List<TaskResponse> responseDtos = taskEntities.stream()
                .map(TaskResponse::new) // For each entity, create a new TaskResponse
                .collect(Collectors.toList()); // Collect them into a new list

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * Updates the status of a specific task.
     *
     * @param taskId The ID of the task to update, taken from the URL path.
     * @param newStatus The new status string, taken from the request body.
     * @return The updated task data as a safe DTO.
     */
 // Inside TaskController.java

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestBody String newStatus, // Receives the raw request body, e.g., "\"COMPLETE\""
            @AuthenticationPrincipal Users currentUser) {

        // v-- ADD THIS LINE TO REMOVE THE QUOTES --v
        String cleanStatus = newStatus.replaceAll("\"", "");

        // Call the service method with the cleaned status
        TaskEntity updatedTaskEntity = taskService.updateTaskStatus(taskId, cleanStatus, currentUser.getUserId());

        TaskResponse responseDto = new TaskResponse(updatedTaskEntity);
        return ResponseEntity.ok(responseDto);
    }
    
    @GetMapping("/{taskId}")
    @PostAuthorize("returnObject.body.assignedTo.username == authentication.name or " +
            "returnObject.body.assignedBy.username == authentication.name")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Integer taskId) {
        // Call the new service method
        TaskEntity taskEntity = taskService.getTaskById(taskId);

        // Convert the entity to a safe DTO for the response
        TaskResponse responseDto = new TaskResponse(taskEntity);

        return ResponseEntity.ok(responseDto);
    }
}