package com.freepass.conference.dto;

import jakarta.validation.constraints.Email;

public class DetailsChangeRequest {

    String username;

    @Email(message = "Email not valid")
    String email;

    String password;

    public DetailsChangeRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
