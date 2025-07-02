package com.ArushyRaina.WorkflowManagement.DTO;

import java.time.LocalDate;

import com.ArushyRaina.WorkflowManagement.entities.TaskEntity; // Import your entity

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Integer taskId;
    private String title;
    private String description;
    private String status;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private UserResponse assignedTo;
    private UserResponse assignedBy;

    // The mapping constructor for the Task
    public TaskResponse(TaskEntity task) {
        this.taskId = task.getTask_id();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.creationDate = task.getCreationDate();
        this.dueDate = task.getDueDate();
        
        // IMPORTANT: Here we use the mapping constructor of the UserResponse DTO!
        if (task.getAssignedTo() != null) {
            this.assignedTo = new UserResponse(task.getAssignedTo());
        }
        if (task.getAssignedBy() != null) {
            this.assignedBy = new UserResponse(task.getAssignedBy());
        }
    }
}