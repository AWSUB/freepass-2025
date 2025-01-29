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

import com.freepass.conference.dto.DefaultResponse;
import com.freepass.conference.dto.DetailsChangeRequest;
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

    @PatchMapping("/details")
    public ResponseEntity<DefaultResponse<User>> changeDetails(
        @RequestBody @Valid DetailsChangeRequest request, 
        @CurrentSecurityContext SecurityContext context
    ) {
        User user = (User) context.getAuthentication().getPrincipal();
        userService.changeDetails(user, request);
        return ResponseEntity.ok().body(DefaultResponse.success(user));
    }

    @GetMapping("/profile")
    public ResponseEntity<DefaultResponse<Profile>> viewProfile(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(DefaultResponse.success(profile));
    }

    @GetMapping("/profile/id/{id}")
    public ResponseEntity<DefaultResponse<Profile>> viewProfile(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(DefaultResponse.success(profile));
    } 

    @GetMapping("/profile/{username}")
    public ResponseEntity<DefaultResponse<Profile>> viewProfile(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(DefaultResponse.success(profile));
    } 

    @GetMapping("/profile/all")
    public ResponseEntity<DefaultResponse<Iterable<Profile>>> viewAllProfile() throws Exception {
        ArrayList<Profile> profiles = new ArrayList<>();
        userService.findAllUser().forEach(user -> profiles.add(user.getProfile()));
        return ResponseEntity.ok().body(DefaultResponse.success(profiles));
    }
    
    @PatchMapping("/profile")
    public ResponseEntity<DefaultResponse<Profile>> updateProfile(
        @RequestBody @Valid ProfileUpdateRequest request, 
        @CurrentSecurityContext SecurityContext context
    ) {
        User user = (User) context.getAuthentication().getPrincipal();
        Profile profile = user.getProfile();
        return ResponseEntity.ok().body(DefaultResponse.success(userService.updateProfile(request, profile)));
    }
}
