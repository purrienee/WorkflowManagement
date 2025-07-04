package com.ArushyRaina.WorkflowManagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORT THIS

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

    // --- vvv THIS IS THE DEFINITIVE FIX vvv ---
    @Transactional(readOnly = true)
    // --- ^^^ THIS IS THE DEFINITIVE FIX ^^^ ---
    public DashboardResponse getDashboardData(Users currentUser) {
        DashboardResponse dashboard = new DashboardResponse();
        
        // This mapping now happens INSIDE the transaction
        dashboard.setUserInfo(new UserResponse(currentUser));

        // These service calls will join the existing transaction
        List<TaskResponse> myTasks = taskService.getTasksForUser(currentUser.getUserId())
                .stream().map(TaskResponse::new).collect(Collectors.toList());
        dashboard.setMyOpenTasks(myTasks);

        List<LeaveRequestResponse> myLeaveRequests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId())
                .stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        dashboard.setMyLeaveRequests(myLeaveRequests);
        
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