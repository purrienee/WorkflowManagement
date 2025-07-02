package com.ArushyRaina.WorkflowManagement.DTO;

import java.time.LocalDate;

import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveRequestResponse {
    private Integer id;
    private UserResponse employee;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private LocalDate submissionDate;

    public LeaveRequestResponse(LeaveRequest leaveRequest) {
        this.id = leaveRequest.getId();
        this.employee = new UserResponse(leaveRequest.getEmployee());
        this.leaveType = leaveRequest.getLeaveType();
        this.startDate = leaveRequest.getStartDate();
        this.endDate = leaveRequest.getEndDate();
        this.reason = leaveRequest.getReason();
        this.status = leaveRequest.getStatus();
        this.submissionDate = leaveRequest.getSubmissionDate();
    }
}
