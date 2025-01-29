package com.freepass.conference.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.freepass.conference.enums.FeedbackRating;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;

@Entity
@Transactional
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JsonBackReference
    private Session session;

    @Column(name = "\"user\"")
    private User user;

    private Date timestamp;

    private String content;

    private FeedbackRating rating;

    public Feedback(User user, String content, FeedbackRating rating) {
        this.user = user;
        this.timestamp = Date.from(Instant.now());
        this.content = content;
        this.rating = rating;
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

    public FeedbackRating getFeedbackRating() {
        return rating;
    }
}
