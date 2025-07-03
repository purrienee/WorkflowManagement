package com.ArushyRaina.WorkflowManagement.DTO;

import com.ArushyRaina.WorkflowManagement.entities.Users;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private Integer userId;
    private String username;
    private String fullname;
    private String email;
    private String role;
    private Boolean isActive;
    private String managerName; // <-- NEW: To display the manager's name

    // The mapping constructor
    public UserResponse(Users user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.role = user.getROLE_user();
        this.isActive = user.getIsActive();

        // Check if the user has a manager and set the name
        if (user.getManager() != null) {
            this.managerName = user.getManager().getFullname();
        }
    }
}