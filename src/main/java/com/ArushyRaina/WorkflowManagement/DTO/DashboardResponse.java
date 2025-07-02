package com.ArushyRaina.WorkflowManagement.DTO;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
// This annotation is key: it will hide any fields that are null in the final JSON.
// This is perfect for hiding manager-only sections from employees.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    // --- Section 1: Information about the logged-in user ---
    private UserResponse userInfo;

    // --- Section 2: Data relevant to ANY user ---
    private List<TaskResponse> myOpenTasks;
    private List<LeaveRequestResponse> myLeaveRequests;

    // --- Section 3: Data ONLY for managers ---
    private List<LeaveRequestResponse> pendingLeaveApprovals;

    // --- Section 4: A guide for what the user can do next ---
    private Map<String, String> availableActions;
}