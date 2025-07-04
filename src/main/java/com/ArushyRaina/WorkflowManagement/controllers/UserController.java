package com.ArushyRaina.WorkflowManagement.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ArushyRaina.WorkflowManagement.DTO.UserCreateRequest;
import com.ArushyRaina.WorkflowManagement.DTO.UserResponse;
import com.ArushyRaina.WorkflowManagement.DTO.UserUpdateRequest;
import com.ArushyRaina.WorkflowManagement.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a list of all users.
     * Only accessible by Admins.
     * URL: GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a single user by their ID.
     * Only accessible by Admins.
     * URL: GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        UserResponse user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user.
     * Only accessible by Admins.
     * URL: POST /api/users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserResponse createdUser = userService.createNewUser(userCreateRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Updates an existing user's details.
     * Only accessible by Admins.
     * URL: PUT /api/users/{userId}
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = userService.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user.
     * Only accessible by Admins.
     * URL: DELETE /api/users/{userId}
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all employees.
     * Accessible by Admins and Managers.
     * URL: GET /api/users/employees
     */
    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<UserResponse>> getAllEmployees() {
        List<UserResponse> employees = userService.findAllByRole("ROLE_EMPLOYEE");
        return ResponseEntity.ok(employees);
    }

    /**
     * Retrieves all managers.
     * Accessible by Admins and Managers.
     * URL: GET /api/users/managers
     */
    @GetMapping("/managers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<UserResponse>> getAllManagers() {
        List<UserResponse> managers = userService.findAllManagers();
        return ResponseEntity.ok(managers);
    }
}
