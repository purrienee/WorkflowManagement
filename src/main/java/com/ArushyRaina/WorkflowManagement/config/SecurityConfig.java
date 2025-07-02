package com.ArushyRaina.WorkflowManagement.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ArushyRaina.WorkflowManagement.services.JpaUserDetailService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables method-level security like @PreAuthorize
public class SecurityConfig {
	
    private final JpaUserDetailService jpaUserDetailService;
	
    public SecurityConfig(JpaUserDetailService jpaUserDetailService) {
        this.jpaUserDetailService = jpaUserDetailService;
    }
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This is the main security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // v-- THIS IS THE FIRST IMPORTANT PART --v
                // Enable CORS using the 'corsConfigurationSource' bean defined below
                .cors(Customizer.withDefaults())
                
                // Disable CSRF protection, as it's not needed for this type of API
                .csrf(csrf -> csrf.disable())

                // Define which requests need to be authorized
             // Inside your securityFilterChain method in SecurityConfig.java

                .authorizeHttpRequests(auth -> auth
                        // This rule says the login page and your CSS file can be accessed by anyone.
                        .requestMatchers("/login.html", "/style.css").permitAll()
                        // This rule says all OTHER requests must be authenticated.
                        .anyRequest().authenticated()
                )
                // ...
                
                // Tell Spring Security to use our custom service for finding users
                .userDetailsService(jpaUserDetailService)

                // Enable the default form-based login
             // New, corrected version
                .formLogin(form -> form
                    .loginPage("/login.html")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/api/dashboard", true) // <-- ADD THIS LINE
                    .permitAll()
                )
                
                .build();
    }

    // v-- THIS IS THE SECOND IMPORTANT PART --v
    // This bean defines the CORS policy for the entire application.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from any origin. For production, you should restrict this
        // to your front-end's actual domain (e.g., "https://my-app.com").
        configuration.setAllowedOrigins(List.of("*"));
        
        // Allow all standard HTTP methods (GET, POST, PUT, DELETE, etc.)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow all headers in the request
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow credentials (like cookies for session management) to be sent
        configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all paths in the application
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}