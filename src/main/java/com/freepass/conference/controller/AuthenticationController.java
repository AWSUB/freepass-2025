package com.freepass.conference.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.dto.UserLoginRequest;
import com.freepass.conference.dto.DefaultResponse;
import com.freepass.conference.dto.JwtTokenResponse;
import com.freepass.conference.dto.UserRegisterRequest;
import com.freepass.conference.model.User;
import com.freepass.conference.service.AuthenticationService;
import com.freepass.conference.service.JwtTokenService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtTokenService jwtTokenService;

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<User>> register(@RequestBody @Valid UserRegisterRequest register) {
        return ResponseEntity.ok().body(DefaultResponse.success(authenticationService.register(register)));
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<JwtTokenResponse>> login(@RequestBody UserLoginRequest login) {
        String token = jwtTokenService.buildToken(authenticationService.authenticate(login));
        return ResponseEntity.ok().body(DefaultResponse.success(
            new JwtTokenResponse(token, jwtTokenService.getExpirationTime())
        ));
    }
}
