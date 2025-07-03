package com.ArushyRaina.WorkflowManagement.DTO;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    private UserResponse userInfo;
    private List<TaskResponse> myOpenTasks;
    private List<LeaveRequestResponse> myLeaveRequests;
    private Map<String, String> availableActions;

    // --- Data ONLY for managers/admins ---
    private List<LeaveRequestResponse> pendingLeaveApprovals;
    private List<TaskResponse> tasksAssignedByMe; // <-- NEW
}