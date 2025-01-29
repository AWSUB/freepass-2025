package com.freepass.conference.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.transaction.Transactional;

@Entity
@Transactional
public class Profile implements Serializable {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;  
    
    private String name;

    private String division;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @MapsId
    @JsonBackReference
    private User user;

    @SuppressWarnings("unused")
    private Profile() {}

    public Profile(User user) {
        this(user, null, null);
    }

    public Profile(User user, String name, String division) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = name;
        this.user = user;
        this.division = division;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String affiliation) {
        this.division = affiliation;
    }

    public User getUser() {
        return user;
    }
}
