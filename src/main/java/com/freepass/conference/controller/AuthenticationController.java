package com.freepass.conference.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.dto.UserLoginRequest;
import com.freepass.conference.dto.UserLoginResponse;
import com.freepass.conference.dto.UserRegisterRequest;
import com.freepass.conference.dto.UserRegisterResponse;
import com.freepass.conference.service.AuthenticationService;
import com.freepass.conference.service.JwtTokenService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtTokenService jwtTokenService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest register) {
        return ResponseEntity.ok().body(
            new UserRegisterResponse(authenticationService.register(register))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest login) {
        String token = jwtTokenService.buildToken(authenticationService.authenticate(login));
        return ResponseEntity.ok().body(
            new UserLoginResponse(token, jwtTokenService.getExpirationTime())
        );
    }
}
