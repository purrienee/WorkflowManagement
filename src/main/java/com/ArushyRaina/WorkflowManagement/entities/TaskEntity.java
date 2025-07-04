package com.ArushyRaina.WorkflowManagement.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- IMPORT THIS

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer task_id;

	private String title;
	
	@Lob
	private String description;
	
	private String status;
	
	private LocalDate creationDate;
	
	private LocalDate dueDate;
	
	@ManyToOne
	@JoinColumn(name= "assigned_to")
    @JsonBackReference("user-tasks-assigned-to") // <-- ADD THIS ANNOTATION
	private Users assignedTo;
	
	@ManyToOne
	@JoinColumn(name= "assigned_By")
    @JsonBackReference("user-tasks-assigned-by") // <-- ADD THIS ANNOTATION
	private Users assignedBy;
}