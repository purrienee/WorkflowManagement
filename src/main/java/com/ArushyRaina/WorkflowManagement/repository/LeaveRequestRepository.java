package com.ArushyRaina.WorkflowManagement.repository;

import java.util.List;
import java.util.Set; // <-- IMPORT THIS

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORT THIS
import org.springframework.data.repository.query.Param; // <-- IMPORT THIS
import org.springframework.stereotype.Repository;

import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    // Find all leave requests submitted by a specific employee
    List<LeaveRequest> findByEmployee(Users employee);

    // Find all leave requests with a specific status (for admins to see all pending requests)
    List<LeaveRequest> findByStatus(String status);
    
    // --- vvv THIS METHOD WAS MISSING - IT IS NOW ADDED vvv ---
    // This custom query finds pending leave requests only for a specific set of employees (a manager's direct reports)
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'PENDING' AND lr.employee IN :directReports")
    List<LeaveRequest> findPendingRequestsForManager(@Param("directReports") Set<Users> directReports);
    // --- ^^^ END OF ADDED METHOD ^^^ ---
}