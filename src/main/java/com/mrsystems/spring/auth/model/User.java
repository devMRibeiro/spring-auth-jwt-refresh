package com.mrsystems.spring.auth.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
		name = "users",
		uniqueConstraints = {
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email")
		})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Correct for PSQL
	@Column(name = "user_id")
	private Long id = null;

	@NotBlank
	@Size(max = 20)
	@Column(name = "user_username")
	private String username = null;

	@NotBlank
	@Size(min = 6, max = 120)
	@Column(name = "user_password")
	private String password = null;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "user_email")
	private String email = null;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
							name = "user_roles",
							joinColumns = @JoinColumn(name = "user_id"),
				      inverseJoinColumns = @JoinColumn(name = "role_id")
      			)
	@Column(name = "user_role")
	private Set<Role> roles = new HashSet<Role>();

	public User(
			@NotBlank @Size(max = 20) String username,
			@NotBlank @Size(min = 6, max = 120) String password,
			@NotBlank @Size(max = 50) String email)
	{
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User() { }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}