package com.freepass.conference.model;

import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private Session session;

    private User user;

    private Date timestamp;

    private String content;

    public Feedback(User user, String content) {
        this.user = user;
        this.timestamp = Date.from(Instant.now());
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public Session getSession() {
        return session;
    }

    public User getUser() {
        return user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}
