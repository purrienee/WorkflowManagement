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
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

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
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveRequestId));
        
        request.setStatus("APPROVED");
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest rejectLeaveRequest(Integer leaveRequestId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveRequestId));

        request.setStatus("REJECTED");
        return leaveRequestRepository.save(request);
    }

    /**
     * Retrieves all leave requests for a specific employee.
     */
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveRequestsForEmployee(Integer employeeId) {
        Users employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: "  + employeeId));
        return leaveRequestRepository.findByEmployee(employee);
    }
    
    /**
     * Retrieves pending leave requests based on the current user's role.
     * This is the method that required the @Transactional annotation to fix the LazyInitializationException.
     */
    @Transactional(readOnly = true)
    public List<LeaveRequest> getPendingLeaveRequestsForUser(Users currentUser) {
        // Admins see all pending requests across the organization.
        if ("ROLE_ADMIN".equals(currentUser.getROLE_user())) {
            return leaveRequestRepository.findByStatus("PENDING");
        }
        
        // Managers see pending requests ONLY from their direct reports.
        if ("ROLE_MANAGER".equals(currentUser.getROLE_user())) {
            // This access to getDirectReports() is what requires an open session.
            if (currentUser.getDirectReports() != null && !currentUser.getDirectReports().isEmpty()) {
                return leaveRequestRepository.findPendingRequestsForManager(currentUser.getDirectReports());
            } else {
                // Return an empty list if the manager has no employees.
                return List.of();
            }
        }
        
        // Employees do not see any pending approvals.
        return List.of();
    }
}