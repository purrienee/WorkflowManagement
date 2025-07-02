package com.ArushyRaina.WorkflowManagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ArushyRaina.WorkflowManagement.DTO.UserCreateRequest;
import com.ArushyRaina.WorkflowManagement.DTO.UserResponse;
import com.ArushyRaina.WorkflowManagement.DTO.UserUpdateRequest;
import com.ArushyRaina.WorkflowManagement.entities.TaskEntity;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.repository.TaskRepository;
import com.ArushyRaina.WorkflowManagement.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor to inject all required dependencies from Spring's context
    public UserService(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Finds all users and converts them to safe UserResponse DTOs.
     * @return A list of UserResponse objects, safe to send to a client.
     */
    public List<UserResponse> findAllUsers() {
        List<Users> users = userRepository.findAll();
        // Use a Java Stream to efficiently map each Users entity to a UserResponse DTO
        return users.stream()
                .map(UserResponse::new) // This calls the mapping constructor: new UserResponse(user)
                .collect(Collectors.toList());
    }

    /**
     * Finds a single user by ID and converts it to a safe UserResponse DTO.
     * @return A UserResponse object.
     */
    public UserResponse findUserById(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return new UserResponse(user);
    }

    /**
     * Creates a new user from a DTO, handles password encoding, and returns a safe response DTO.
     */
    @Transactional
    public UserResponse createNewUser(UserCreateRequest request) {
        // Prevent creating users with duplicate usernames
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username '" + request.getUsername() + "' is already taken.");
        }

        Users newUser = new Users();
        newUser.setUsername(request.getUsername());
        newUser.setFullname(request.getFullname());
        newUser.setEmail(request.getEmail());
        newUser.setPassword_hash(passwordEncoder.encode(request.getPassword())); // Securely hash the password
        newUser.setROLE_user(request.getRole());
        newUser.setIsActive(true); // New users are active by default
        newUser.setManagerId(request.getManagerId());
        
        

        Users savedUser = userRepository.save(newUser);
        
        // Return the safe DTO, not the entity with the password hash
        return new UserResponse(savedUser);
    }

    /**
     * Updates an existing user's details from a DTO.
     */
    @Transactional
    public UserResponse updateUser(Integer userId, UserUpdateRequest request) {
        Users existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Update fields only if new values are provided in the request
        if (request.getFullname() != null) {
            existingUser.setFullname(request.getFullname());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            existingUser.setROLE_user(request.getRole());
        }
        if (request.getIsActive() != null) {
            existingUser.setIsActive(request.getIsActive());
        }

        Users updatedUser = userRepository.save(existingUser);
        return new UserResponse(updatedUser);
    }
    
    

    /**
     * Deletes a user and safely handles their associated tasks.
     */
 // Inside UserService.java

    @Transactional
    public void deleteUser(Integer userId) {
        // 1. Find the user to be deleted. Throws an exception if not found.
        Users userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Handle tasks assigned BY this user (if they are a manager).
        // We find these tasks and set their 'assignedBy' field to null
        // so the tasks themselves are not deleted.
        List<TaskEntity> tasksAssignedBy = taskRepository.findByAssignedBy(userToDelete);
        for (TaskEntity task : tasksAssignedBy) {
            task.setAssignedBy(null);
            // The @Transactional annotation will ensure this change is saved.
        }

        // 3. Handle tasks assigned TO this user.
        // First, we find all tasks assigned to this user.
        List<TaskEntity> tasksAssignedTo = taskRepository.findByAssignedTo(userToDelete);
        
        // Now, we delete this collection of tasks.
        if (!tasksAssignedTo.isEmpty()) {
            taskRepository.deleteAll(tasksAssignedTo);
        }

        // 4. Finally, delete the user themselves.
        userRepository.delete(userToDelete);
    }
    
    public List<UserResponse> findAllEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> "ROLE_EMPLOYEE".equals(user.getROLE_user()))
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> findAllManagers() {
        return userRepository.findAll().stream()
                .filter(user -> "ROLE_MANAGER".equals(user.getROLE_user()))
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
}