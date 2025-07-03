package com.ArushyRaina.WorkflowManagement.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestCreate;
import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.repository.LeaveRequestRepository;
import com.ArushyRaina.WorkflowManagement.repository.UserRepository;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
    }

 // ...
    public LeaveRequest createLeaveRequest(LeaveRequestCreate request, Integer employeeId) {
        Users employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getManager() == null) {
            throw new IllegalStateException("Cannot apply for leave. You have not been assigned a manager.");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setManager(employee.getManager()); // This line is the key.
        // ... set other fields and save
        return leaveRequestRepository.save(leaveRequest);
    }
    // ...s

    @Transactional
    public LeaveRequest approveLeaveRequest(Integer leaveRequestId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        request.setStatus("APPROVED");
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest rejectLeaveRequest(Integer leaveRequestId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        request.setStatus("REJECTED");
        return leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getLeaveRequestsForEmployee(Integer employeeId) {
        Users employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return leaveRequestRepository.findByEmployee(employee);
    }

    public List<LeaveRequest> getPendingLeaveRequestsForUser(Users currentUser) {
        if ("ROLE_ADMIN".equals(currentUser.getROLE_user())) {
            // Admin sees all pending requests for oversight
            return leaveRequestRepository.findByStatus("PENDING");
        }
        
        if ("ROLE_MANAGER".equals(currentUser.getROLE_user())) {
            // Manager sees requests assigned specifically to them
            return leaveRequestRepository.findByManagerAndStatus(currentUser, "PENDING");
        }
        
        return List.of();
    }
}