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

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and assigns a new task from a manager to an employee.
     * This method writes to the database, so it uses @Transactional.
     */
    @Transactional
    public TaskEntity assignTask(String title, String description, LocalDate dueDate, Integer assignedToId, Integer assignedById) {
        Users employee = userRepository.findById(assignedToId)
                .orElseThrow(() -> new RuntimeException("Cannot assign task: Employee not found with ID: " + assignedToId));
        
        Users manager = userRepository.findById(assignedById)
                .orElseThrow(() -> new RuntimeException("Cannot assign task: Manager not found with ID: " + assignedById));

        TaskEntity newTask = new TaskEntity();
        newTask.setTitle(title);
        newTask.setDescription(description);
        newTask.setDueDate(dueDate);
        newTask.setCreationDate(LocalDate.now());
        newTask.setStatus("PENDING");
        
        newTask.setAssignedTo(employee);
        newTask.setAssignedBy(manager);

        return taskRepository.save(newTask);
    }

    /**
     * Updates the status of an existing task.
     * This method writes to the database, so it uses @Transactional.
     */
    @Transactional
    public TaskEntity updateTaskStatus(Integer taskId, String newStatus, Integer currentUserId) {
        TaskEntity existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Authorization check: Only the assignee can update the status.
        if (!existingTask.getAssignedTo().getUserId().equals(currentUserId)) {
            throw new SecurityException("User is not authorized to update this task.");
        }

        existingTask.setStatus(newStatus);
        return taskRepository.save(existingTask);
    }

    /**
     * Retrieves all tasks assigned to a specific user.
     * This is a read-only operation, but @Transactional keeps the session open for lazy loading.
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksForUser(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cannot find tasks: User not found with ID: " + userId));
        return taskRepository.findByAssignedTo(user);
    }
    
    /**
     * Retrieves a single task by its ID.
     */
    @Transactional(readOnly = true)
    public TaskEntity getTaskById(Integer taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
    }
    
    /**
     * Retrieves all tasks assigned BY a specific manager.
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksAssignedByManager(Integer managerId) {
        Users manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
        return taskRepository.findByAssignedBy(manager);
    }
}