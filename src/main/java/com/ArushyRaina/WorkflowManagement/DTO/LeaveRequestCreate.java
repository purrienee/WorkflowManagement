package com.ArushyRaina.WorkflowManagement.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT THIS

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
    // vvv THIS IS THE FIX vvv
    // Tell Jackson exactly what JSON key to look for
    @JsonProperty("startDate")
    private LocalDate startDate;
    // ^^^ END OF FIX ^^^

    @NotNull(message = "End date is required.")
    @FutureOrPresent(message = "End date cannot be in the past.")
    // vvv THIS IS THE FIX vvv
    // Tell Jackson exactly what JSON key to look for
    @JsonProperty("endDate")
    private LocalDate endDate;
    // ^^^ END OF FIX ^^^

    private String reason;
}