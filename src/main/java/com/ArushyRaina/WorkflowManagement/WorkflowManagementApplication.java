package com.ArushyRaina.WorkflowManagement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ArushyRaina.WorkflowManagement.entities.Users;
import com.ArushyRaina.WorkflowManagement.repository.UserRepository;

@SpringBootApplication
public class WorkflowManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowManagementApplication.class, args);
		
		Users users = new Users();
		users.setFullname("Arushy Raina");
		users.getFullname();
		
		System.out.println("Hello World");
		
		
	}
	
	// Inside WorkflowManagementApplication.java

	@Bean
	public CommandLineRunner initialUserData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	    return args -> {
	        // Create Admin user
	        if (userRepository.findByUsername("admin").isEmpty()) {
	            Users admin = new Users();
	            admin.setUsername("admin");
	            admin.setFullname("Admin User");
	            admin.setPassword_hash(passwordEncoder.encode("adminpass"));
	            admin.setROLE_user("ROLE_ADMIN"); // Spring Security convention needs "ROLE_" prefix
	            admin.setIsActive(true);
	            userRepository.save(admin);
	            System.out.println( "Created ADMIN user: admin");
	        }

	        // Create Manager user
	        if (userRepository.findByUsername("manager").isEmpty()) {
	            Users manager = new Users();
	            manager.setUsername("manager");
	            manager.setFullname("Manager User");
	            manager.setPassword_hash(passwordEncoder.encode("managerpass"));
	            manager.setROLE_user("ROLE_MANAGER");
	            manager.setIsActive(true);
	            userRepository.save(manager);
	            System.out.println(" Created MANAGER user: manager");
	        }

	        // Create Employee user
	        if (userRepository.findByUsername("employee").isEmpty()) {
	            Users employee = new Users();
	            employee.setUsername("employee");
	            employee.setFullname("Employee User");
	            employee.setPassword_hash(passwordEncoder.encode("emppass"));
	            employee.setROLE_user("ROLE_EMPLOYEE");
	            employee.setIsActive(true);
	            userRepository.save(employee);
	            System.out.println(" Created EMPLOYEE user: employee");
	        }
	    };
	}

}
