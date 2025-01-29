package com.freepass.conference.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.freepass.conference.dto.DetailsChangeRequest;
import com.freepass.conference.dto.ProfileUpdateRequest;
import com.freepass.conference.model.Profile;
import com.freepass.conference.model.User;
import com.freepass.conference.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public User changeDetails(User user, DetailsChangeRequest request) {
        if (request.getUsername() != null && !request.getUsername().isEmpty()) user.setUsername(request.getUsername());
        if (request.getUsername() != null && !request.getEmail().isEmpty()) user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public Profile updateProfile(ProfileUpdateRequest request, Profile profile) {
        profile.setName(request.getName());
        profile.setDivision(request.getDivision());
        userRepository.save(profile.getUser());
        return profile;
    }

    public Iterable<User> findAllUser() {
        return userRepository.findAll();
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}