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

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"manager", "directReports"}) // Prevent recursion issues in Lombok methods
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

    // --- vvv NEW HIERARCHY RELATIONSHIPS vvv ---

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private Users manager;

    @OneToMany(mappedBy = "manager")
    @JsonManagedReference // The "one" side of the relationship
    private Set<Users> directReports;

    // --- ^^^ NEW HIERARCHY RELATIONSHIPS ^^^ ---

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(this.ROLE_user));
	}

	@Override
	public String getPassword() {
		return this.password_hash;
	}

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", Fullname='" + Fullname + '\'' +
                '}';
    }
}