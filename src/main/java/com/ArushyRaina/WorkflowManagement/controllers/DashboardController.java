package com.ArushyRaina.WorkflowManagement.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ArushyRaina.WorkflowManagement.DTO.DashboardResponse;
import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.services.DashboardService; // Ensure this import is correct

// vvv THIS IS THE FIX: Changed from @Service to @RestController vvv
@RestController
@RequestMapping("/api")
public class DashboardController {

    // The Controller calls the Service
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // This creates the /api/dashboard endpoint
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal Users currentUser) {
        // The business logic is now in the service
        DashboardResponse dashboardData = dashboardService.getDashboardData(currentUser);
        return ResponseEntity.ok(dashboardData);
    }
}