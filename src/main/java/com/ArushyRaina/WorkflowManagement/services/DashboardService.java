package com.ArushyRaina.WorkflowManagement.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ArushyRaina.WorkflowManagement.DTO.DashboardResponse;
import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestResponse;
import com.ArushyRaina.WorkflowManagement.DTO.TaskResponse;
import com.ArushyRaina.WorkflowManagement.DTO.UserResponse;
import com.ArushyRaina.WorkflowManagement.entities.Users;

@Service
public class DashboardService {

    private final TaskService taskService;
    private final LeaveRequestService leaveRequestService;

    public DashboardService(TaskService taskService, LeaveRequestService leaveRequestService) {
        this.taskService = taskService;
        this.leaveRequestService = leaveRequestService;
    }

    public DashboardResponse getDashboardData(Users currentUser) {
        DashboardResponse dashboard = new DashboardResponse();
        Map<String, String> actions = new HashMap<>();

        // --- 1. Populate universal data for all users ---
        dashboard.setUserInfo(new UserResponse(currentUser));

        // Get all tasks assigned to the current user and convert them to DTOs
        List<TaskResponse> myTasks = taskService.getTasksForUser(currentUser.getUserId())
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
        dashboard.setMyOpenTasks(myTasks);

        // Get all leave requests for the current user and convert them to DTOs
        List<LeaveRequestResponse> myLeaveRequests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId())
                .stream()
                .map(LeaveRequestResponse::new)
                .collect(Collectors.toList());
        dashboard.setMyLeaveRequests(myLeaveRequests);
        
        // --- 2. Populate data and actions specific to roles ---

        // Actions for ALL authenticated users
        actions.put("SUBMIT_LEAVE_REQUEST", "POST /api/leave/apply");

        // If the user is a MANAGER, add manager-specific data and actions
        if (currentUser.getROLE_user().equals("ROLE_MANAGER")) {
            // Add manager-specific data
            List<LeaveRequestResponse> pendingApprovals = leaveRequestService.getPendingLeaveRequests()
                    .stream()
                    .map(LeaveRequestResponse::new)
                    .collect(Collectors.toList());
            dashboard.setPendingLeaveApprovals(pendingApprovals);

            // Add manager-specific actions
            actions.put("ASSIGN_NEW_TASK", "POST /api/tasks");
            actions.put("APPROVE_LEAVE_REQUEST", "POST /api/leave/{id}/approve");
            actions.put("REJECT_LEAVE_REQUEST", "POST /api/leave/{id}/reject");
        }

        // If the user is an ADMIN, add admin-specific actions
        if (currentUser.getROLE_user().equals("ROLE_ADMIN")) {
            actions.put("VIEW_ALL_USERS", "GET /api/users");
            actions.put("CREATE_NEW_USER", "POST /api/users");
        }

        dashboard.setAvailableActions(actions);
        
        return dashboard;
    }
}