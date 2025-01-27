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
import com.freepass.conference.enums.SessionStatus;
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

    @GetMapping("active/id/{id}")
    public ResponseEntity<Session> viewActiveSession(@PathVariable Integer id) throws Exception {
        Session session = sessionService.findSessionById(id);
        if (session.getStatus() == SessionStatus.PROPOSAL) throw new Exception("Session still on proposal status");
        return ResponseEntity.ok().body(sessionService.findSessionById(id));
    }
    
    @GetMapping("/active/all")
    public ResponseEntity<Iterable<Session>> viewAllActiveSession() {
        return ResponseEntity.ok().body(sessionService.findAllActiveSession());
    }

    @GetMapping("/proposal/{id}")
    public ResponseEntity<Session> viewSessionProposal(@PathVariable Integer id) throws Exception {
        Session session = sessionService.findSessionById(id);
        if (session.getStatus() != SessionStatus.PROPOSAL) throw new Exception("Session already active");
        return ResponseEntity.ok().body(sessionService.findSessionById(id));
    }    
    
    @GetMapping("/proposal/all")
    public ResponseEntity<Iterable<Session>> viewAllSessionProposals() {
        return ResponseEntity.ok().body(sessionService.findAllSessionProposal());
    }

    @PostMapping("active/id/{id}/register")
    public ResponseEntity<Session> registerSession(
        @PathVariable Integer id, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.registerSession(id, user));
    }

    @PostMapping("active/id/{id}/feedback")
    public ResponseEntity<Feedback> giveFeedback(
        @PathVariable Integer id,
        @RequestBody FeedbackRequest request,
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.giveFeedback(id, request, user));
    }

    @GetMapping("active/id/{sessionId}/feedback/{feedbackId}")
    public ResponseEntity<Feedback> viewFeedback(
        @PathVariable Integer sessionId,
        @PathVariable Integer feedbackId
    ) throws Exception {
       return ResponseEntity.ok().body(sessionService.viewFeedback(sessionId, feedbackId)); 
    }
    
}
