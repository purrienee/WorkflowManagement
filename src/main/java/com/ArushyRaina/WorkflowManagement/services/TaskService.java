package com.ArushyRaina.WorkflowManagement.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ArushyRaina.WorkflowManagement.entities.TaskEntity;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.repository.TaskRepository;
import com.ArushyRaina.WorkflowManagement.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // Constructor to inject the repositories Spring manages
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and assigns a new task from a manager to an employee.
     */
    @Transactional
    public TaskEntity assignTask(String title, String description, LocalDate dueDate, Integer assignedToId, Integer assignedById) {
        // 1. Find the employee and manager User objects from the database
        Users employee = userRepository.findById(assignedToId)
                .orElseThrow(() -> new RuntimeException("Cannot assign task: Employee not found with ID: " + assignedToId));
        
        Users manager = userRepository.findById(assignedById)
                .orElseThrow(() -> new RuntimeException("Cannot assign task: Manager not found with ID: " + assignedById));

        // 2. Create a new TaskEntity object and set its properties
        TaskEntity newTask = new TaskEntity();
        newTask.setTitle(title);
        newTask.setDescription(description);
        newTask.setDueDate(dueDate);
        newTask.setCreationDate(LocalDate.now());
        newTask.setStatus("PENDING"); // New tasks always start as PENDING
        
        // 3. Set the relationships using the User objects we found
        newTask.setAssignedTo(employee);
        newTask.setAssignedBy(manager);

        // 4. Save the fully-formed task to the database and return it
        return taskRepository.save(newTask);
    }

    /**
     * Updates the status of an existing task.
     */
    @Transactional
    public TaskEntity updateTaskStatus(Integer taskId, String newStatus, Integer currentUserId) {
        // Step 1: Find the task from the database using its ID.
        TaskEntity existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Step 2: Perform the critical authorization check.
        // Verify that the ID of the user making the request is the same as the ID of the user the task is assigned to.
        if (!existingTask.getAssignedTo().getUserId().equals(currentUserId)) {
            // If the IDs do not match, the user is not authorized. Stop the process immediately.
            throw new SecurityException("User is not authorized to update this task.");
        }

        // Step 3: If the security check passes, update the task's status.
        existingTask.setStatus(newStatus);

        // Step 4: Save the updated task back to the database and return it.
        // Because the method is @Transactional, this save is safe and efficient.
        return taskRepository.save(existingTask);
    }

    /**
     * Retrieves all tasks assigned to a specific user.
     */
    public List<TaskEntity> getTasksForUser(Integer userId) {
        // 1. Find the user object
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cannot find tasks: User not found with ID: " + userId));
        
        // 2. Use our custom repository method to find all tasks for that user
        return taskRepository.findByAssignedTo(user);
    }
    
    public TaskEntity getTaskById(Integer taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
    }
}