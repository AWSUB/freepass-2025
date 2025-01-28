package com.freepass.conference.model;

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
public class Profile {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;  
    
    private String name;

    private String affiliation;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @MapsId
    @JsonBackReference
    private User user;

    @SuppressWarnings("unused")
    private Profile() {}

    public Profile(User user) {
        this(user, null, null);
    }

    public Profile(User user, String name, String affiliation) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = name;
        this.user = user;
        this.affiliation = affiliation;
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

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public User getUser() {
        return user;
    }
}
