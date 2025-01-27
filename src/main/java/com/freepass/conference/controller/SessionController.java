package com.freepass.conference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.dto.FeedbackRequest;
import com.freepass.conference.dto.SessionRequest;
import com.freepass.conference.model.Feedback;
import com.freepass.conference.model.Session;
import com.freepass.conference.model.User;
import com.freepass.conference.service.SessionService;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @PostMapping("")
    public ResponseEntity<Session> createSession(
        @RequestBody SessionRequest request, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.createSession(request, user));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Session> viewSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(sessionService.findSessionById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<Iterable<Session>> viewAllActiveSession() {
        return ResponseEntity.ok().body(sessionService.findAllActiveSession());
    }
    
    @GetMapping("/proposals")
    public ResponseEntity<Iterable<Session>> viewAllSessionProposals() {
        return ResponseEntity.ok().body(sessionService.findAllSessionProposal());
    }

    @PostMapping("/id/{id}/register")
    public ResponseEntity<Session> registerSession(
        @PathVariable Integer id, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.registerSession(id, user));
    }

    @PostMapping("/id/{id}/feedback")
    public ResponseEntity<Feedback> giveFeedback(
        @PathVariable Integer id,
        @RequestBody FeedbackRequest request,
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.giveFeedback(id, request, user));
    }
}
