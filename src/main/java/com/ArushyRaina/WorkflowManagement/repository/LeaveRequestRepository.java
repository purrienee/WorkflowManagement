package com.ArushyRaina.WorkflowManagement.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ArushyRaina.WorkflowManagement.entities.LeaveRequest;
import com.ArushyRaina.WorkflowManagement.entities.Users;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    List<LeaveRequest> findByEmployee(Users employee);
    List<LeaveRequest> findByStatus(String status);

    // Find pending requests where the employee is one of the manager's direct reports
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'PENDING' AND lr.employee IN :directReports")
    List<LeaveRequest> findPendingRequestsForManager(@Param("directReports") Set<Users> directReports);
}