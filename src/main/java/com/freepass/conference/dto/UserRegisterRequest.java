package com.freepass.conference.dto;

import jakarta.validation.constraints.Email;

public class UserRegisterRequest {

    private String username;
    
    private String password;

    @Email(message = "Invalid email")
    private String email;

    public UserRegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
