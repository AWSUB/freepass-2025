package com.freepass.conference.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;

public class SessionRequest implements Serializable {

    private String title;

    private String description;

    private Integer seatsAvailable;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Future(message = "Request not valid")
    private Date registrationDateStart;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Future(message = "Request not valid")
    private Date sessionStart;

    private Long sessionTime;

    public SessionRequest(
        String title,
        String description,
        Integer seatsAvailable,
        Date registrationDateStart,
        Date sessionStart,
        Long sessionTime
    ) {
        this.title = title;
        this.description = description;
        this.seatsAvailable = seatsAvailable;
        this.registrationDateStart = registrationDateStart;
        this.sessionStart = sessionStart;
        this.sessionTime = sessionTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSeatsAvailable() {
        return seatsAvailable;
    }

    public Date getRegistrationDateStart() {
        return registrationDateStart;
    }

    public Date getSessionStart() {
        return sessionStart;
    }

    public Long getSessionTime() {
        return sessionTime;
    }

    @AssertTrue(message = "Request not valid")
    private boolean isRequestValid() {
        if (seatsAvailable <= 0) return false;
        if (sessionStart.before(registrationDateStart)) return false;
        if (sessionTime <= 0) return false;
        return true;
    }
}
