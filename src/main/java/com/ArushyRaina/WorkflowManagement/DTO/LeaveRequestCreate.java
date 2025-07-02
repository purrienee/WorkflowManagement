package com.ArushyRaina.WorkflowManagement.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveRequestCreate {
    @NotBlank(message = "Leave type is required.")
    private String leaveType;

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    @FutureOrPresent(message = "End date cannot be in the past.")
    private LocalDate endDate;

    private String reason;
}