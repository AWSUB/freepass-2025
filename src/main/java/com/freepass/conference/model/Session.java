package com.freepass.conference.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freepass.conference.enums.SessionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;

@Entity
@Transactional
public class Session implements Serializable {

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

    @ManyToOne
    @JsonBackReference
    private User user;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "session_registered_users",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> registeredUser;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
        this.user = user;
        this.registeredUser = new ArrayList<User>();
        this.feedbacks = new ArrayList<Feedback>();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
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
        if (!registeredUser.contains(user)) {
            registeredUser.add(user);
            user.getUserCreatedSession().add(this);
        }
    }

    public void removeUser(User currentUser) {
        registeredUser.removeIf(user -> user.getId().equals(currentUser.getId()));
        seatsAvailable++;
    
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

    public User getUser() {
        return user;
    }

    @JsonProperty("userCreator")
    public Profile getProfile() {
        return user.getProfile();
    }

    public List<User> getRegisteredUser() {
        return registeredUser;
    }

    @JsonProperty("registeredUser")
    public List<Profile> getRegisteredUserProfile() {
        ArrayList<Profile> userProfiles = new ArrayList<>();
        registeredUser.forEach(user -> {
            userProfiles.add(user.getProfile());
        });
        return userProfiles;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    @Transactional
    public void addFeedback(Feedback feedback) {
        this.feedbacks.add(feedback);
    }

    @Override
    public boolean equals(Object obj) {
        return (
            obj instanceof Session session &&
            session.getId() == this.getId()
        );
    }
}
