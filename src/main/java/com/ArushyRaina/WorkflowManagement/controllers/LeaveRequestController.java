package com.ArushyRaina.WorkflowManagement.controllers;

import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestCreate;
import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestResponse;
import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.services.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // Endpoint for an employee to submit a leave request
    @PostMapping("/apply")
    public ResponseEntity<LeaveRequestResponse> applyForLeave(
            @Valid @RequestBody LeaveRequestCreate request,
            @AuthenticationPrincipal Users currentUser) {
        
        LeaveRequest createdRequest = leaveRequestService.createLeaveRequest(request, currentUser.getUserId());
        return new ResponseEntity<>(new LeaveRequestResponse(createdRequest), HttpStatus.CREATED);
    }

    // Endpoint for an employee to see their own leave requests
    @GetMapping("/my-requests")
    public ResponseEntity<List<LeaveRequestResponse>> getMyLeaveRequests(@AuthenticationPrincipal Users currentUser) {
        List<LeaveRequest> requests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId());
        List<LeaveRequestResponse> response = requests.stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Endpoint for a manager to see all PENDING leave requests
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveRequestResponse>> getPendingRequests() {
        List<LeaveRequest> requests = leaveRequestService.getPendingLeaveRequests();
        List<LeaveRequestResponse> response = requests.stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Endpoint for a manager to approve a leave request
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveRequestResponse> approveRequest(@PathVariable Integer id) {
        LeaveRequest approvedRequest = leaveRequestService.approveLeaveRequest(id);
        return ResponseEntity.ok(new LeaveRequestResponse(approvedRequest));
    }

    // Endpoint for a manager to reject a leave request
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveRequestResponse> rejectRequest(@PathVariable Integer id) {
        LeaveRequest rejectedRequest = leaveRequestService.rejectLeaveRequest(id);
        return ResponseEntity.ok(new LeaveRequestResponse(rejectedRequest));
    }
}