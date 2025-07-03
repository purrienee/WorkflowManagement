package com.ArushyRaina.WorkflowManagement.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"assignedTo", "assignedBy"}) // Prevent loops
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "assigned_to")
    @ToString.Exclude // CRITICAL FIX: Exclude to prevent chain reaction
	private Users assignedTo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "assigned_By")
    @ToString.Exclude // CRITICAL FIX: Exclude to prevent chain reaction
	private Users assignedBy;
}