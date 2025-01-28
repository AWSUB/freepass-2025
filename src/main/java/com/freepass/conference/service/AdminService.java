package com.freepass.conference.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.freepass.conference.model.User;
import com.freepass.conference.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    public User addCoordinator(User user) {
        user.addAuthority(new SimpleGrantedAuthority("ROLE_COORDINATOR"));
        return userRepository.save(user); 
    }

    public User removeCoordinator(User user) {
        user.removeAuthority("ROLE_COORDINATOR");
        return userRepository.save(user);
    }

    public User removeUser(User user) throws Exception {
        try {
            sessionService.removeSession(user.getCurrentCreatedSession());
            user.getCurrentParticipatedSession().removeUser(user);
        } finally {
            userRepository.deleteById(user.getId());
        }
        return user;
    }
}
