package com.freepass.conference.dto;

import jakarta.validation.constraints.NotNull;

public class ProfileUpdateRequest {

    @NotNull
    private String name;

    @NotNull
    private String division;

    public ProfileUpdateRequest(String name, String division) {
        this.name = name;
        this.division = division;
    }

    public String getName() {
        return name;
    }

    public String getDivision() {
        return division;
    }
}
