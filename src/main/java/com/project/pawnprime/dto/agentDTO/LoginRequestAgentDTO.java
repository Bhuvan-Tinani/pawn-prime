package com.project.pawnprime.dto.agentDTO;

public class LoginRequestAgentDTO {
	private String username; // email for agent
    private String password;
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
	public LoginRequestAgentDTO(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public LoginRequestAgentDTO() {
		super();
	}


}
