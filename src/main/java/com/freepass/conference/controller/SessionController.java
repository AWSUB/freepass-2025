package com.freepass.conference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/session")
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

    @GetMapping("/all")
    public ResponseEntity<Iterable<Session>> getAllSession() {
        return ResponseEntity.ok().body(sessionService.findAllSession());
    }
    

    @GetMapping("/me/all")
    public ResponseEntity<Iterable<Session>> viewMySession(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.findSessionByUser(user));
    }

    @GetMapping("/me/current")
    public ResponseEntity<Session> viewCurrentSession(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(user.getCurrentCreatedSession());
    }

    @PatchMapping("/me/current")
    public ResponseEntity<Session> updateCurrentSession(
        @RequestBody @Valid SessionRequest sessionRequest, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        Session session = user.getCurrentCreatedSession();
        return ResponseEntity.ok().body(sessionService.updateSession(sessionRequest, session));
    }

    @DeleteMapping("/me/current")
    public ResponseEntity<Session> removeCurrentSession(
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        Session session = user.getCurrentCreatedSession();
        return ResponseEntity.ok().body(sessionService.removeSession(session));
    }

    @GetMapping("/proposal/{id}")
    public ResponseEntity<Session> viewSessionProposal(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(sessionService.findSessionProposalById(id));
    }

    @GetMapping("/proposal/{id}/accept")
    public ResponseEntity<Session> acceptSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(sessionService.approveSession(id));
    }

    @GetMapping("/proposal/{id}/reject")
    public ResponseEntity<Session> rejectSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(sessionService.rejectSession(id));
    }
    
    @GetMapping("/proposal/all")
    public ResponseEntity<Iterable<Session>> viewAllSessionProposals() {
        return ResponseEntity.ok().body(sessionService.findAllSessionProposal());
    }

    @GetMapping("/active/id/{id}")
    public ResponseEntity<Session> viewActiveSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(sessionService.findActiveSessionById(id));
    }

    @DeleteMapping("/active/id/{id}")
    public ResponseEntity<Session> deleteSession(@PathVariable Integer id) throws Exception {
        Session session = sessionService.findActiveSessionById(id);
        return ResponseEntity.ok().body(sessionService.removeSession(session));
    }
    
    @GetMapping("/active/all")
    public ResponseEntity<Iterable<Session>> viewAllActiveSession() {
        return ResponseEntity.ok().body(sessionService.findAllActiveSession());
    }

    @PostMapping("/active/id/{id}/register")
    public ResponseEntity<Session> registerSession(
        @PathVariable Integer id, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.registerSession(id, user));
    }

    @PostMapping("/active/id/{id}/feedback")
    public ResponseEntity<Feedback> giveFeedback(
        @PathVariable Integer id,
        @RequestBody FeedbackRequest request,
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(sessionService.giveFeedback(id, request, user));
    }

    @GetMapping("/active/id/{sessionId}/feedback/{feedbackId}")
    public ResponseEntity<Feedback> viewFeedback(
        @PathVariable Integer sessionId,
        @PathVariable Integer feedbackId
    ) throws Exception {
       return ResponseEntity.ok().body(sessionService.viewFeedback(sessionId, feedbackId)); 
    }

    @DeleteMapping("/active/id/{sessionId}/feedback/{feedbackId}")
    public ResponseEntity<Feedback> deleteFeedback(
        @PathVariable Integer sessionId,
        @PathVariable Integer feedbackId        
    ) throws Exception {
        return ResponseEntity.ok().body(sessionService.deleteFeedback(sessionId, feedbackId)); 
    }
}
