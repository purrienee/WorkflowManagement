package com.ArushyRaina.WorkflowManagement.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<LeaveRequestResponse> applyForLeave(
            @Valid @RequestBody LeaveRequestCreate request,
            @AuthenticationPrincipal Users currentUser) {
        LeaveRequest createdRequest = leaveRequestService.createLeaveRequest(request, currentUser.getUserId());
        return new ResponseEntity<>(new LeaveRequestResponse(createdRequest), HttpStatus.CREATED);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<LeaveRequestResponse>> getMyLeaveRequests(@AuthenticationPrincipal Users currentUser) {
        List<LeaveRequest> requests = leaveRequestService.getLeaveRequestsForEmployee(currentUser.getUserId());
        List<LeaveRequestResponse> response = requests.stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Endpoint for a manager/admin to see PENDING leave requests
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')") // <-- ALLOW ADMIN
    public ResponseEntity<List<LeaveRequestResponse>> getPendingRequests(@AuthenticationPrincipal Users currentUser) { // <-- GET CURRENT USER
        List<LeaveRequest> requests = leaveRequestService.getPendingLeaveRequestsForUser(currentUser); // <-- USE NEW SERVICE METHOD
        List<LeaveRequestResponse> response = requests.stream().map(LeaveRequestResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // A manager or admin can approve a leave request
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponse> approveRequest(@PathVariable Integer id) {
        LeaveRequest approvedRequest = leaveRequestService.approveLeaveRequest(id);
        return ResponseEntity.ok(new LeaveRequestResponse(approvedRequest));
    }

    // A manager or admin can reject a leave request
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponse> rejectRequest(@PathVariable Integer id) {
        LeaveRequest rejectedRequest = leaveRequestService.rejectLeaveRequest(id);
        return ResponseEntity.ok(new LeaveRequestResponse(rejectedRequest));
    }
}