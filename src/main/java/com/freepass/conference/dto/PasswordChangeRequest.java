package com.freepass.conference.dto;

public class PasswordChangeRequest {

    String password;

    public PasswordChangeRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
