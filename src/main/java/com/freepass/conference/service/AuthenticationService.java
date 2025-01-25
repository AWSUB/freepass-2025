package com.freepass.conference.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.freepass.conference.dto.UserLoginRequest;
import com.freepass.conference.dto.UserRegisterRequest;
import com.freepass.conference.model.User;
import com.freepass.conference.repository.UserRepository;

@Service
public class AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public User register(UserRegisterRequest register) {
        return userRepository.save(new User(
            register.getUsername(),
            register.getEmail(),
            passwordEncoder.encode(register.getPassword()),
            new SimpleGrantedAuthority("ROLE_USER")
        ));
    }

    public User authenticate(UserLoginRequest login) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                login.getUsername(),
                login.getPassword()
            )
        );

        return userRepository.findByUsername(login.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
