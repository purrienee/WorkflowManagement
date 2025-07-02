package com.ArushyRaina.WorkflowManagement.entities;


import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class Users implements UserDetails{
     
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer userId;
	
	@Column(nullable = false, unique = true)
	private String username	;
	
	private String password_hash;
	
	private String Fullname;
	
	@Column(unique = true)
	private String email;
	
	private String ROLE_user;
	
	private Integer managerId;
	
	private Boolean isActive = true;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Collections.singletonList(new SimpleGrantedAuthority(this.ROLE_user));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password_hash;
	}

	

	
}