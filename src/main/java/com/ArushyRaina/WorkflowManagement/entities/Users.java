package com.ArushyRaina.WorkflowManagement.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
// CRITICAL FIX: Exclude relational fields from equals and hashCode to prevent loops
@EqualsAndHashCode(exclude = {"manager", "directReports"})
@Table(name = "Users")
public class Users implements UserDetails {
     
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	private String password_hash;
	
	private String Fullname;
	
	@Column(unique = true)
	private String email;
	
	private String ROLE_user;
	
	private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching is often safer for this
    @JoinColumn(name = "manager_id")
    @JsonBackReference
    @ToString.Exclude // CRITICAL FIX: Exclude from toString() to prevent StackOverflowError on logging
    private Users manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.EAGER) // Eager fetch can help in some service scenarios
    @JsonManagedReference
    @ToString.Exclude // CRITICAL FIX: Exclude from toString()
    private Set<Users> directReports;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(this.ROLE_user));
	}

	@Override
	public String getPassword() {
		return this.password_hash;
	}
}