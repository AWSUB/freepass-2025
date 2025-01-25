package com.freepass.conference.dto;

import java.time.Instant;
import java.util.Date;

public class UserLoginResponse {

    private String token;

    private Long expirationTime;

    private Date issuedAt;

    private Date expirationDate;

    public UserLoginResponse(String token, Long expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.issuedAt = Date.from(Instant.now());
        this.expirationDate = Date.from(Instant.now().plusMillis(expirationTime));
    }

    public String getToken() {
        return token;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}
