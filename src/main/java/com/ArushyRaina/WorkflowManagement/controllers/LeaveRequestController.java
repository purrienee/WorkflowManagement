package com.ArushyRaina.WorkflowManagement.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORT THIS
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestCreate;
import com.ArushyRaina.WorkflowManagement.DTO.LeaveRequestResponse;
import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.services.LeaveRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping("/apply")
    @Transactional // <-- THE DEFINITIVE FIX IS HERE
    public ResponseEntity<LeaveRequestResponse> applyForLeave(
            @Valid @RequestBody LeaveRequestCreate request,
            @AuthenticationPrincipal Users currentUser) {
        
        // This call will now happen inside an open database transaction
        LeaveRequest createdRequest = leaveRequestService.createLeaveRequest(request, currentUser.getUserId());
        
        // The DTO can now safely access lazy-loaded fields because the transaction is still active
        LeaveRequestResponse response = new LeaveRequestResponse(createdRequest);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<LeaveRequestResponse>> getMyLeaveRequests(@AuthenticationPrincipal Users currentUser) {
        List<LeaveRequest> requests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId());
        List<LeaveRequestResponse> response = requests.stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<LeaveRequestResponse>> getPendingRequests(@AuthenticationPrincipal Users currentUser) {
        List<LeaveRequest> requests = leaveRequestService.getPendingLeaveRequestsForUser(currentUser);
        List<LeaveRequestResponse> response = requests.stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponse> approveRequest(@PathVariable Integer id) {
        LeaveRequest approvedRequest = leaveRequestService.approveLeaveRequest(id);
        return ResponseEntity.ok(new LeaveRequestResponse(approvedRequest));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponse> rejectRequest(@PathVariable Integer id) {
        LeaveRequest rejectedRequest = leaveRequestService.rejectLeaveRequest(id);
        return ResponseEntity.ok(new LeaveRequestResponse(rejectedRequest));
    }
}