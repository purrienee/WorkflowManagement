package com.ArushyRaina.WorkflowManagement.services;

import java.time.LocalDate;
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

    @Transactional
    public LeaveRequest createLeaveRequest(LeaveRequestCreate request, Integer employeeId) {
        Users employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus("PENDING");
        leaveRequest.setSubmissionDate(LocalDate.now());

        return leaveRequestRepository.save(leaveRequest);
    }

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

    // --- vvv MODIFIED METHOD vvv ---
    public List<LeaveRequest> getPendingLeaveRequestsForUser(Users currentUser) {
        // If the user is an ADMIN, they should see all pending requests.
        if ("ROLE_ADMIN".equals(currentUser.getROLE_user())) {
            return leaveRequestRepository.findByStatus("PENDING");
        }
        
        // If the user is a MANAGER, they see requests from their direct reports.
        if ("ROLE_MANAGER".equals(currentUser.getROLE_user())) {
            if (currentUser.getDirectReports() == null || currentUser.getDirectReports().isEmpty()) {
                return List.of(); // Return empty list if manager has no reports
            }
            return leaveRequestRepository.findPendingRequestsForManager(currentUser.getDirectReports());
        }
        
        // Employees see no pending approvals.
        return List.of();
    }
}