package com.ArushyRaina.WorkflowManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    List<LeaveRequest> findByEmployee(Users employee);
    List<LeaveRequest> findByStatus(String status);
    
    // --- vvv THE NEW METHOD IS HERE vvv ---
    // Find all leave requests where the specified user is the manager and the status is pending.
    List<LeaveRequest> findByManagerAndStatus(Users manager, String status);
    // --- ^^^ END OF NEW METHOD ^^^ ---
}