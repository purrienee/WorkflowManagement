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

    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus("PENDING");
    }
}