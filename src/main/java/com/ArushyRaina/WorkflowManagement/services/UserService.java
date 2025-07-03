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

    public UserService(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse findUserById(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse createNewUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username '" + request.getUsername() + "' is already taken.");
        }

        Users newUser = new Users();
        newUser.setUsername(request.getUsername());
        newUser.setFullname(request.getFullname());
        newUser.setEmail(request.getEmail());
        newUser.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        newUser.setROLE_user(request.getRole());
        newUser.setIsActive(true);
        
        // --- vvv NEW LOGIC TO SET MANAGER vvv ---
        if (request.getManagerId() != null) {
            Users manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + request.getManagerId()));
            // Ensure the selected user is actually a manager
            if (!"ROLE_MANAGER".equals(manager.getROLE_user())) {
                 throw new IllegalStateException("The selected user is not a manager.");
            }
            newUser.setManager(manager);
        }
        // --- ^^^ NEW LOGIC TO SET MANAGER ^^^ ---

        Users savedUser = userRepository.save(newUser);
        return new UserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Integer userId, UserUpdateRequest request) {
        Users existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

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
    
    @Transactional
    public void deleteUser(Integer userId) {
        Users userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<TaskEntity> tasksAssignedBy = taskRepository.findByAssignedBy(userToDelete);
        for (TaskEntity task : tasksAssignedBy) {
            task.setAssignedBy(null);
        }

        List<TaskEntity> tasksAssignedTo = taskRepository.findByAssignedTo(userToDelete);
        if (!tasksAssignedTo.isEmpty()) {
            taskRepository.deleteAll(tasksAssignedTo);
        }

        userRepository.delete(userToDelete);
    }
    
    public List<UserResponse> findAllByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> role.equals(user.getROLE_user()))
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    // Renamed from findAllManagers for clarity
    public List<UserResponse> findAllManagers() {
        return findAllByRole("ROLE_MANAGER");
    }
}