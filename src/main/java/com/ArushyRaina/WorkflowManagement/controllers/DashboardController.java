package com.ArushyRaina.WorkflowManagement.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.ArushyRaina.WorkflowManagement.DTO.DashboardResponse;
import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestResponse;
import com.ArushyRaina.WorkflowManagement.DTO.TaskResponse;
import com.ArushyRaina.WorkflowManagement.DTO.UserResponse;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.services.LeaveRequestService;
import com.ArushyRaina.WorkflowManagement.services.TaskService;

@Service
public class DashboardController {

    private final TaskService taskService;
    private final LeaveRequestService leaveRequestService;

    // The constructor should only take the services it uses.
    public DashboardController(TaskService taskService, LeaveRequestService leaveRequestService) {
        this.taskService = taskService;
        this.leaveRequestService = leaveRequestService;
    }

    // This is the method that your DashboardController is trying to call.
    // This definition makes it available and resolves the error.
    public DashboardResponse getDashboardData(Users currentUser) {
        DashboardResponse dashboard = new DashboardResponse();
        
        // 1. Populate data that is common to all users
        dashboard.setUserInfo(new UserResponse(currentUser));

        List<TaskResponse> myTasks = taskService.getTasksForUser(currentUser.getUserId())
                .stream().map(TaskResponse::new).collect(Collectors.toList());
        dashboard.setMyOpenTasks(myTasks);

        List<LeaveRequestResponse> myLeaveRequests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId())
                .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        dashboard.setMyLeaveRequests(myLeaveRequests);
        
        // 2. Initialize role-specific lists to be safe and avoid nulls
        dashboard.setPendingLeaveApprovals(new ArrayList<>());
        dashboard.setTasksAssignedByMe(new ArrayList<>());

        String role = currentUser.getROLE_user();

        // 3. Populate data for MANAGERS
        if ("ROLE_MANAGER".equals(role)) {
            List<LeaveRequestResponse> pendingApprovals = leaveRequestService.getPendingLeaveRequestsForUser(currentUser)
                    .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
            dashboard.setPendingLeaveApprovals(pendingApprovals);
            
            List<TaskResponse> assignedTasks = taskService.getTasksAssignedByManager(currentUser.getUserId())
                    .stream().map(TaskResponse::new).collect(Collectors.toList());
            dashboard.setTasksAssignedByMe(assignedTasks);
        }

        // 4. Populate data for ADMINS (for oversight)
        if ("ROLE_ADMIN".equals(role)) {
            List<LeaveRequestResponse> pendingApprovals = leaveRequestService.getPendingLeaveRequestsForUser(currentUser)
                    .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
            dashboard.setPendingLeaveApprovals(pendingApprovals);
        }

        return dashboard;
    }
}