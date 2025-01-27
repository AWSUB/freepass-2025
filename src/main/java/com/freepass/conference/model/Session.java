package com.freepass.conference.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freepass.conference.enums.SessionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    private String description;

    private Integer seatsAvailable;

    private Date registrationDateStart;
    
    private Date sessionStart;

    private Long sessionTime;

    private SessionStatus status;

    private User userCreator;

    private List<User> registeredUser;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Feedback> feedbacks;

    @SuppressWarnings("unused")
    private Session() {}

    public Session(
        String title, 
        String description, 
        Integer seatsAvailable,
        Date registrationDateStart,
        Date sessionStart,
        Long sessionTime,
        User user
    ) {
        this.title = title;
        this.description = description;
        this.seatsAvailable = seatsAvailable;
        this.registrationDateStart = registrationDateStart;
        this.sessionStart = sessionStart;
        this.sessionTime = sessionTime;
        this.status = SessionStatus.PROPOSAL;
        this.userCreator = user;
        this.registeredUser = new ArrayList<User>();
        this.feedbacks = new ArrayList<Feedback>();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitile(String title) {
        this.title = title;
    }

    public String getDecription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSeatsAvailable() {
        return seatsAvailable;
    }

    public void assignSeat(User user) throws Exception {
        if (seatsAvailable <= 0) {
            throw new Exception("Seats already full");
        }
        seatsAvailable--;
        registeredUser.add(user);
    }

    public Date getRegistrationDateStart() {
        return registrationDateStart;
    }

    public void setRegistrationDateStart(Date date) {
        this.registrationDateStart = date;
    }

    public Date getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(Date date) {
        this.sessionStart = date;
    }

    public Long getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(Long sessionTime) {
        this.sessionTime = sessionTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public User getUserCreator() {
        return userCreator;
    }

    public List<User> getRegisteredUser() {
        return registeredUser;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void addFeedback(Feedback feedback) {
        this.feedbacks.add(feedback);
    }
}
