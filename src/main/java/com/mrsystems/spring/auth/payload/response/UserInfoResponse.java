package com.mrsystems.spring.auth.payload.response;

import java.util.List;

public class UserInfoResponse {

	private Long id;
	private String username;
	private String email;
	private List<String> listRoles;

	public UserInfoResponse(Long id, String username, String email, List<String> alRoles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.listRoles = alRoles;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getListRoles() {
		return listRoles;
	}

	public void setListRoles(List<String> alRoles) {
		this.listRoles = alRoles;
	}
}