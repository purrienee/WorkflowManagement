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
 // Inside SecurityConfig.java

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // v-- THIS IS THE CRITICAL CHANGE --v
                .authorizeHttpRequests(auth -> auth
                        // Allow access to the login page, all files in the css/ and js/ folders, and any third-party libraries you might add.
                        .requestMatchers("/", "/login.html", "/css/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                        // All other requests (including all /api/** endpoints) must be authenticated.
                        .anyRequest().authenticated()
                )
                // ^-- THIS IS THE CRITICAL CHANGE --^

                .userDetailsService(jpaUserDetailService)

                .formLogin(form -> form
                    .loginPage("/login.html") // The custom login page
                    .loginProcessingUrl("/login") // The URL the form should POST to
                    .defaultSuccessUrl("/index.html", true) // On success, redirect to the main app page
                    .failureUrl("/login.html?error=true") // On failure, redirect back with an error
                    .permitAll()
                )

                // Add explicit logout configuration
                .logout(logout -> logout
                    .logoutUrl("/logout") // The URL to trigger logout
                    .logoutSuccessUrl("/login.html?logout=true") // Where to go after logout
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
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