package com.freepass.conference.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freepass.conference.dto.ProfileUpdateRequest;
import com.freepass.conference.model.Profile;
import com.freepass.conference.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Profile updateProfile(ProfileUpdateRequest request, Profile profile) {
        if (request.getUsername() != null) profile.setUsername(request.getUsername());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getName() != null) profile.setName(request.getName());
        userRepository.save(profile.getUser());
        return profile;
    }
}