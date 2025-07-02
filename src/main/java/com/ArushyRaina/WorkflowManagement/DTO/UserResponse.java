package com.ArushyRaina.WorkflowManagement.DTO;

import com.ArushyRaina.WorkflowManagement.entities.Users; // Make sure to import your Users entity

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Good for testing
@NoArgsConstructor  // Good for frameworks and flexibility
public class UserResponse {
    private Integer userId;
    private String username;
    private String fullname;
    private String email;
    private Integer managerId;

    /**
     * This is a "mapping constructor".
     * It knows how to build a safe UserResponse object from a full,
     * sensitive Users entity by only copying the public fields.
     */
    public UserResponse(Users user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.managerId = user.getManagerId();
    } // <-- Make sure this constructor has its closing brace '}'
}