package com.ArushyRaina.WorkflowManagement.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "leave_requests")
@EqualsAndHashCode(exclude = {"employee", "manager"}) // Prevent loops
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    @ToString.Exclude
    private Users employee;
    
    // --- vvv THE FIX IS HERE vvv ---
    @ManyToOne(fetch = FetchType.EAGER)
    
    @JoinColumn(name = "manager_id") // This will hold the ID of the manager responsible for approval
    @ToString.Exclude
    private Users manager;
    // --- ^^^ END OF FIX ^^^ ---

    @Column(nullable = false)
    private String leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String reason;

    @Column(nullable = false)
    private String status;

    private LocalDate submissionDate;
}