package com.freepass.conference.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthConfiguration {

    SecurityFilterChain filterChain(HttpSecurity http) {
        return null;
    }
}
