package com.freepass.conference.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.dto.PasswordChangeRequest;
import com.freepass.conference.dto.ProfileUpdateRequest;
import com.freepass.conference.model.Profile;
import com.freepass.conference.model.User;
import com.freepass.conference.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<User> changePassword(
        @RequestBody @Valid PasswordChangeRequest request, 
        @CurrentSecurityContext SecurityContext context
    ) {
        User user = (User) context.getAuthentication().getPrincipal();
        userService.changePassword(user, request);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<Profile> viewProfile(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(profile);
    }

    @GetMapping("/profile/id/{id}")
    public ResponseEntity<Profile> viewProfile(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(profile);
    } 

    @GetMapping("/profile/{username}")
    public ResponseEntity<Profile> viewProfile(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(profile);
    } 

    @GetMapping("/profile/all")
    public ResponseEntity<Iterable<Profile>> viewAllProfile() throws Exception {
        ArrayList<Profile> profiles = new ArrayList<>();
        userService.findAllUser().forEach(user -> profiles.add(user.getProfile()));
        return ResponseEntity.ok().body(profiles);
    }
    
    @PatchMapping("/profile")
    public ResponseEntity<Profile> updateProfile(
        @RequestBody @Valid ProfileUpdateRequest request, 
        @CurrentSecurityContext SecurityContext context
    ) {
        User user = (User) context.getAuthentication().getPrincipal();
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(userService.updateProfile(request, profile));
    }
}
