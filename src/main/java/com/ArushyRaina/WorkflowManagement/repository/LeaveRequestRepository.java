package com.ArushyRaina.WorkflowManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    // Find all leave requests submitted by a specific employee
    List<LeaveRequest> findByEmployee(Users employee);

    // Find all leave requests with a specific status (for managers to see pending requests)
    List<LeaveRequest> findByStatus(String status);
}