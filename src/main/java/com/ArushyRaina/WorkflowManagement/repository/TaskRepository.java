package com.ArushyRaina.WorkflowManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ArushyRaina.WorkflowManagement.entities.TaskEntity;
import com.ArushyRaina.WorkflowManagement.entities.Users;



@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer>{
	
	List<TaskEntity> findByAssignedTo(Users user);

	// This method finds all TASKS created BY a given USER (manager).
	List<TaskEntity> findByAssignedBy(Users user);

	@Modifying
	@Query("DELETE FROM TaskEntity t WHERE t.assignedTo = :user")
	void deleteAllTasksForUser(Users user);
}
