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
@EqualsAndHashCode(exclude = {"manager", "directReports", "tasksAssignedTo", "tasksAssignedBy"})
@Table(name = "Users")
public class Users implements UserDetails {
     
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	// vvv THIS FIELD WAS MISSING - IT IS NOW RESTORED vvv
	private String password_hash;
	// ^^^ THIS FIELD WAS MISSING - IT IS NOW RESTORED ^^^
	
	private String Fullname;
	
	@Column(unique = true)
	private String email;
	
	private String ROLE_user;
	
	private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    @JsonBackReference
    @ToString.Exclude
    private Users manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.EAGER)
    @JsonManagedReference
    @ToString.Exclude
    private Set<Users> directReports;

    @OneToMany(mappedBy = "assignedTo")
    @JsonManagedReference("user-tasks-assigned-to")
    @ToString.Exclude
    private Set<TaskEntity> tasksAssignedTo;

    @OneToMany(mappedBy = "assignedBy")
    @JsonManagedReference("user-tasks-assigned-by")
    @ToString.Exclude
    private Set<TaskEntity> tasksAssignedBy;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(this.ROLE_user));
	}

	@Override
	public String getPassword() {
		// This method now correctly returns the restored field
		return this.password_hash;
	}
}