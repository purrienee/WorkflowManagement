package com.ArushyRaina.WorkflowManagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, message = "Username must be at least 3 characters long.")
    private String username;

    @NotBlank(message = "Full name is required.")
    private String fullname;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @NotBlank(message = "Role is required.")
    private String role; // e.g., "ROLE_EMPLOYEE" or "ROLE_MANAGER"
    
    
    private Integer managerId;
}