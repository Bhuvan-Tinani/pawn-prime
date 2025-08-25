package com.project.pawnprime.dto.adminDTO;
public class LoginRequestAdmin {
    private String username;
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
	public LoginRequestAdmin(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public LoginRequestAdmin() {
		super();
	}
    
    
    // getters & setters
}