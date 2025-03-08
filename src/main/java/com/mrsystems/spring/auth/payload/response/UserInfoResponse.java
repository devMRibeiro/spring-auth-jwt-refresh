package com.mrsystems.spring.auth.payload.response;

import java.util.ArrayList;

public class UserInfoResponse {

	private Integer id;
	private String username;
	private String email;
	private ArrayList<String> alRoles;

	public UserInfoResponse(Integer id, String username, String email, ArrayList<String> alRoles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.alRoles = alRoles;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public ArrayList<String> getAlRoles() {
		return alRoles;
	}

	public void setAlRoles(ArrayList<String> alRoles) {
		this.alRoles = alRoles;
	}
}