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

import com.freepass.conference.dto.DefaultResponse;
import com.freepass.conference.dto.FeedbackRequest;
import com.freepass.conference.dto.SessionRequest;
import com.freepass.conference.model.Feedback;
import com.freepass.conference.model.Session;
import com.freepass.conference.model.User;
import com.freepass.conference.service.SessionService;
import com.freepass.conference.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    UserService userService;

    @PostMapping("")
    public ResponseEntity<DefaultResponse<Session>> createSession(
        @RequestBody SessionRequest request,
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.createSession(request, user)));
    }

    @GetMapping("/all")
    public ResponseEntity<DefaultResponse<Iterable<Session>>> getAllSession() {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.findAllSession()));
    }
    

    @GetMapping("/me/all")
    public ResponseEntity<DefaultResponse<Iterable<Session>>> viewMySession(@CurrentSecurityContext SecurityContext context) {
        User user = (User) context.getAuthentication().getPrincipal();
        User currentUser = userService.findUserById(user.getId());
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.findSessionByUser(currentUser)));
    }

    @GetMapping("/me/current")
    public ResponseEntity<DefaultResponse<Session>> viewCurrentSession(@CurrentSecurityContext SecurityContext context) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        User currentUser = userService.findUserById(user.getId());
        Session session = currentUser.getCurrentCreatedSession();
        Session currentSession = sessionService.findSessionById(session.getId());
        return ResponseEntity.ok().body(DefaultResponse.success(currentSession));
    }

    @PatchMapping("/me/current")
    public ResponseEntity<DefaultResponse<Session>> updateCurrentSession(
        @RequestBody @Valid SessionRequest sessionRequest, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        User currentUser = userService.findUserById(user.getId());
        Session session = currentUser.getCurrentCreatedSession();
        Session currentSession = sessionService.findSessionById(session.getId());
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.updateSession(sessionRequest, currentSession)));
    }

    @DeleteMapping("/me/current")
    public ResponseEntity<DefaultResponse<Session>> removeCurrentSession(
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        User currentUser = userService.findUserById(user.getId());
        Session session = currentUser.getCurrentCreatedSession();
        Session currentSession = sessionService.findSessionById(session.getId());
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.removeSession(currentSession)));
    }

    @GetMapping("/proposal/{id}")
    public ResponseEntity<DefaultResponse<Session>> viewSessionProposal(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.findSessionProposalById(id)));
    }

    @PatchMapping("/proposal/{id}/accept")
    public ResponseEntity<DefaultResponse<Session>> acceptSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.approveSession(id)));
    }

    @PatchMapping("/proposal/{id}/reject")
    public ResponseEntity<DefaultResponse<Session>> rejectSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.rejectSession(id)));
    }
    
    @GetMapping("/proposal/all")
    public ResponseEntity<DefaultResponse<Iterable<Session>>> viewAllSessionProposals() {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.findAllSessionProposal()));
    }

    @GetMapping("/active/id/{id}")
    public ResponseEntity<DefaultResponse<Session>> viewActiveSession(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.findActiveSessionById(id)));
    }

    @DeleteMapping("/active/id/{id}")
    public ResponseEntity<DefaultResponse<Session>> deleteSession(@PathVariable Integer id) throws Exception {
        Session session = sessionService.findActiveSessionById(id);
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.removeSession(session)));
    }
    
    @GetMapping("/active/all")
    public ResponseEntity<DefaultResponse<Iterable<Session>>> viewAllActiveSession() {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.findAllActiveSession()));
    }

    @PostMapping("/active/id/{id}/register")
    public ResponseEntity<DefaultResponse<Session>> registerSession(
        @PathVariable Integer id, 
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.registerSession(id, user)));
    }

    @PostMapping("/active/id/{id}/feedback")
    public ResponseEntity<DefaultResponse<Session>> giveFeedback(
        @PathVariable Integer id,
        @RequestBody FeedbackRequest request,
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.giveFeedback(id, request, user)));
    }

    @GetMapping("/active/id/{sessionId}/feedback/{feedbackId}")
    public ResponseEntity<DefaultResponse<Feedback>> viewFeedback(
        @PathVariable Integer sessionId,
        @PathVariable Integer feedbackId
    ) throws Exception {
       return ResponseEntity.ok().body(DefaultResponse.success(sessionService.viewFeedback(sessionId, feedbackId))); 
    }

    @DeleteMapping("/active/id/{sessionId}/feedback/{feedbackId}")
    public ResponseEntity<DefaultResponse<Feedback>> deleteFeedback(
        @PathVariable Integer sessionId,
        @PathVariable Integer feedbackId        
    ) throws Exception {
        return ResponseEntity.ok().body(DefaultResponse.success(sessionService.deleteFeedback(sessionId, feedbackId))); 
    }
}
