package com.freepass.conference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.dto.ProfileUpdateRequest;
import com.freepass.conference.model.Profile;
import com.freepass.conference.model.User;
import com.freepass.conference.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseEntity<User> getUser(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<Profile> getProfile(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(profile);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Profile> updateProfile(
        @RequestBody ProfileUpdateRequest request, 
        @CurrentSecurityContext SecurityContext context
    ) {
        User user = (User) context.getAuthentication().getPrincipal();
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(userService.updateProfile(request, profile));
    }
}
