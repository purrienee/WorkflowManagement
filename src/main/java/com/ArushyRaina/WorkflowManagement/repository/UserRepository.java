package com.ArushyRaina.WorkflowManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ArushyRaina.WorkflowManagement.entities.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {
	
	Optional<Users> findByUsername(String Username);

}
