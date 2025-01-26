package com.freepass.conference.dto;

public class ProfileUpdateRequest {

    private String username;

    private String email;

    private String name;

    public ProfileUpdateRequest(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }   
}
