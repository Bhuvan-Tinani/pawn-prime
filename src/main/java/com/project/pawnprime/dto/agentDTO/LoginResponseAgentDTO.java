package com.project.pawnprime.dto.agentDTO;

public class LoginResponseAgentDTO {
	private String token;
    private String username;
    private String role;
    private long id;
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public LoginResponseAgentDTO(String token, String username, String role, long id) {
		super();
		this.token = token;
		this.username = username;
		this.role = role;
		this.id = id;
	}
	public LoginResponseAgentDTO() {
		super();
	}


}
