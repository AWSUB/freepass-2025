package com.freepass.conference.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "\"user\"")
@Transactional
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Set<GrantedAuthority> authorities;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonManagedReference
    private Profile profile;

    @SuppressWarnings("unused")
    private User() {}

    public User(String username, String email, String password, GrantedAuthority... authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = new HashSet<>(Set.of(authorities));
        this.profile = new Profile(this);
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    //profile must be changed manually
    protected void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void addAuthority(GrantedAuthority authority) {
        authorities.add(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //profile must be changed manually
    protected void setUsername(String username) {
        this.username = username;
    }

    public Profile getProfile() {
        return profile;
    }
}
