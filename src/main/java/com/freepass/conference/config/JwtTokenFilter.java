package com.freepass.conference.config;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.freepass.conference.model.User;
import com.freepass.conference.service.JwtTokenService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter{

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if(auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = auth.substring(7);
            Claims claims = jwtTokenService.parseToken(token);

            if (claims.getExpiration().before(new Date())) {
                throw new Exception("Token expired");
            }

            if (claims.getIssuedAt().after(new Date())) {
                throw new Exception("Token issued in the future");
            }

            Integer id = Integer.parseInt(claims.getId());
            String username = claims.getSubject();
            String email = claims.get("email", String.class);
            User user = (User) userDetailsService.loadUserByUsername(username);
            if (user.getId() != id || !user.getUsername().equals(username) || !user.getEmail().equals(email)) {
                throw new Exception("Invalid token");
            }

            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext.getAuthentication() == null) {
                securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}
