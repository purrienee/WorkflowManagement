package com.ArushyRaina.WorkflowManagement.DTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    // We only include fields that an admin should be allowed to update.
    // Username and password changes should be handled by separate, more secure processes.
    
    private String fullname;

    @Email(message = "Please provide a valid email address.")
    private String email;
    
    private String role;
    
    private Boolean isActive;
}