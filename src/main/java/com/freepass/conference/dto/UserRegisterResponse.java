package com.freepass.conference.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.freepass.conference.model.User;

public class UserRegisterResponse {

    private Integer id;

    private String username;

    private String email;
    
    private Collection<? extends GrantedAuthority> authorities;

    public UserRegisterResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.authorities = user.getAuthorities();
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Collection<?> getAuthorities() {
        return authorities;
    }
}
