package com.freepass.conference.dto;

import jakarta.validation.constraints.NotNull;

public class ProfileUpdateRequest {

    @NotNull
    private String name;

    @NotNull
    private String affiliation;

    public ProfileUpdateRequest(String name, String affiliation) {
        this.name = name;
        this.affiliation = affiliation;
    }

    public String getName() {
        return name;
    }

    public String getAffiliation() {
        return affiliation;
    }
}
