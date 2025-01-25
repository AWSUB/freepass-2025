package com.freepass.conference.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.transaction.Transactional;

//TODO: add later
@Entity
@Transactional
public class Profile {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;  
    
    private String name;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @MapsId
    @JsonBackReference
    private User user;

    @SuppressWarnings("unused")
    private Profile() {}

    public Profile(User user) {
        this(user, null);
    }

    public Profile(User user, String name) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = name;
        this.user = user;       
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.user.setUsername(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.user.setEmail(email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
