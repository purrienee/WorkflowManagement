package com.ArushyRaina.WorkflowManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ArushyRaina.WorkflowManagement.services.JpaUserDetailService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class Securityconfig {
	
	public JpaUserDetailService jpaUserDetailService ;
	
	public Securityconfig(JpaUserDetailService jpaUserDetailService) {
		// TODO Auto-generated constructor stub
		this.jpaUserDetailService = jpaUserDetailService;
	}

	
	
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        // We are defining a "chain" of security rules.
        return http
                // Rule 1: Disable CSRF protection for now to keep things simple.
                .csrf(csrf -> csrf.disable())

                // Rule 2: Set up the authorization rules for URLs.
                .authorizeHttpRequests(auth -> auth
                        // For now, our rule is simple: ANY request must be authenticated.
                        .anyRequest().authenticated()
                )
                
                // Rule 3: This is the most critical line.
                // We are telling Spring Security to use OUR UserDetailsService.
                .userDetailsService(jpaUserDetailService)

                // Rule 4: Enable the default form-based login page.
                .formLogin(formLogin -> formLogin.permitAll())
                
                // Finally, build the configured chain.
                .build();
	}
}
