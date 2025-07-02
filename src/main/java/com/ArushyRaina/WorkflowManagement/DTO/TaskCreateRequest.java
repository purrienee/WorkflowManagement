package com.ArushyRaina.WorkflowManagement.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data; // <-- Make sure this import is present

@Data // <-- THIS ANNOTATION IS CRITICAL
public class TaskCreateRequest {
	@NotBlank(message = "Task title cannot be blank.")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters.")
    private String title;

    @Size(max = 500, message = "Description cannot be longer than 500 characters.")
    private String description;

    @NotNull(message = "Due date is required.")
    @Future(message = "Due date must be in the future.")
    private LocalDate dueDate;

    @NotNull(message = "Task must be assigned to a user.")
    private Integer assignedToId;
}