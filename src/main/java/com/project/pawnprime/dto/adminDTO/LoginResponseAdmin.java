package com.project.pawnprime.dto.adminDTO;

public class LoginResponseAdmin {
    private String token;
    private String username;
    private String role;

    public LoginResponseAdmin(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

	public String getToken() {
		return token;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}
    
}