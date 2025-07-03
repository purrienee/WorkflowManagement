package com.ArushyRaina.WorkflowManagement.services;

import java.util.ArrayList;
import java.util.List;
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
    // The dependency on UserService has been REMOVED to break the circular dependency.

    public DashboardService(TaskService taskService, LeaveRequestService leaveRequestService) {
        this.taskService = taskService;
        this.leaveRequestService = leaveRequestService;
    }

    public DashboardResponse getDashboardData(Users currentUser) {
        DashboardResponse dashboard = new DashboardResponse();
        
        dashboard.setUserInfo(new UserResponse(currentUser));

        List<TaskResponse> myTasks = taskService.getTasksForUser(currentUser.getUserId())
                .stream().map(TaskResponse::new).collect(Collectors.toList());
        dashboard.setMyOpenTasks(myTasks);

        List<LeaveRequestResponse> myLeaveRequests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId())
                .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        dashboard.setMyLeaveRequests(myLeaveRequests);
        
        dashboard.setPendingLeaveApprovals(new ArrayList<>());
        dashboard.setTasksAssignedByMe(new ArrayList<>());

        String role = currentUser.getROLE_user();

        if ("ROLE_MANAGER".equals(role)) {
            List<LeaveRequestResponse> pendingApprovals = leaveRequestService.getPendingLeaveRequestsForUser(currentUser)
                    .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
            dashboard.setPendingLeaveApprovals(pendingApprovals);
            
            List<TaskResponse> assignedTasks = taskService.getTasksAssignedByManager(currentUser.getUserId())
                    .stream().map(TaskResponse::new).collect(Collectors.toList());
            dashboard.setTasksAssignedByMe(assignedTasks);
        }

        if ("ROLE_ADMIN".equals(role)) {
            List<LeaveRequestResponse> pendingApprovals = leaveRequestService.getPendingLeaveRequestsForUser(currentUser)
                    .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
            dashboard.setPendingLeaveApprovals(pendingApprovals);
        }

        return dashboard;
    }
}