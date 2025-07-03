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
        System.out.println("Hello World");
    }

    @Bean
    public CommandLineRunner initialUserData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // --- Create Admin user ---
            if (userRepository.findByUsername("admin").isEmpty()) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setFullname("Admin User");
                admin.setPassword_hash(passwordEncoder.encode("adminpass"));
                admin.setROLE_user("ROLE_ADMIN");
                admin.setIsActive(true);
                userRepository.save(admin);
                System.out.println(">>> Created ADMIN user: admin");
            }

            // --- Create Manager user ---
            Users managerUser = userRepository.findByUsername("manager").orElseGet(() -> {
                Users newManager = new Users();
                newManager.setUsername("manager");
                newManager.setFullname("Manager User");
                newManager.setPassword_hash(passwordEncoder.encode("managerpass"));
                newManager.setROLE_user("ROLE_MANAGER");
                newManager.setIsActive(true);
                userRepository.save(newManager);
                System.out.println(">>> Created MANAGER user: manager");
                return newManager;
            });

            // --- Create Employee user AND ASSIGN THE MANAGER ---
            if (userRepository.findByUsername("aru").isEmpty()) {
                Users employee = new Users();
                employee.setUsername("aru");
                employee.setFullname("Aru Employee");
                employee.setPassword_hash(passwordEncoder.encode("aruraina"));
                employee.setROLE_user("ROLE_EMPLOYEE");
                employee.setIsActive(true);
                
                // --- THIS IS THE CRITICAL FIX ---
                // Set the manager object for the employee
                employee.setManager(managerUser);
                // --- END OF CRITICAL FIX ---
                
                userRepository.save(employee);
                System.out.println(">>> Created EMPLOYEE user: aru, assigned to manager: " + managerUser.getUsername());
            }
        };
    }
}