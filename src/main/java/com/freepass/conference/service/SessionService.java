package com.freepass.conference.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.freepass.conference.dto.FeedbackRequest;
import com.freepass.conference.dto.SessionRequest;
import com.freepass.conference.enums.SessionStatus;
import com.freepass.conference.model.Feedback;
import com.freepass.conference.model.Session;
import com.freepass.conference.model.User;
import com.freepass.conference.repository.SessionRepository;
import com.freepass.conference.repository.UserRepository;

@Service
public class SessionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public Session createSession(SessionRequest request, User user) throws Exception {
        if (user.getCurrentCreatedSession() != null) throw new Exception("User already proposed a session or has an active session");
        Session session = new Session(
            request.getTitle(),
            request.getDescription(), 
            request.getSeatsAvailable(), 
            request.getRegistrationDateStart(), 
            request.getSessionStart(), 
            request.getSessionTime(), 
            user
        );
        session.getUser().setCurrentCreatedSession(session);
        session.getUser().addUserCreatedSession(session);
        Session savedSession = sessionRepository.save(session);
        user.setCurrentCreatedSession(savedSession);
        userRepository.save(user);
        return savedSession;
    }

    public Iterable<Session> findAllSession() {
        return sessionRepository.findAll();
    }

    public Iterable<Session> findAllSessionProposal() {
        return sessionRepository.findByStatus(SessionStatus.PROPOSAL);
    }

    public Iterable<Session> findAllActiveSession() {
        return sessionRepository.findByStatusExcept(SessionStatus.PROPOSAL);
    }

    public Session findActiveSessionById(Integer id) throws Exception {
        for (Session session : findAllActiveSession()) {
            if (session.getId() == id) return session;
        }
        throw new Exception("Active session not found");
    }

    public Session findSessionProposalById(Integer id) throws Exception {
        for (Session session : findAllActiveSession()) {
            if (session.getId() == id) return session;
        }
        throw new Exception("Session proposal not found");
    }

    public Iterable<Session> findSessionByUser(User user) {
        return sessionRepository.findByUser(user);
    }

    public Session updateSession(SessionRequest request, Session session) throws Exception {
        if (session.getStatus() == SessionStatus.PROPOSAL) {
            session.setTitle(request.getTitle());
            session.setDescription(request.getDescription());
            session.setRegistrationDateStart(request.getRegistrationDateStart());
            session.setSessionStart(request.getSessionStart());
            session.setSessionTime(request.getSessionTime());
        }
        else if (session.getStatus() != SessionStatus.FINISHED) {
            session.setTitle(request.getTitle());
            session.setDescription(request.getDescription());
        }
        else throw new Exception("Finished session cannot be updated");
        return sessionRepository.save(session);
    }

    public Session removeSession(Session session) throws Exception {
        if (session.getStatus() == SessionStatus.FINISHED) {
            throw new Exception("Finished session cannot be removed");
        }
        sessionRepository.delete(session);
        session.getUser().setCurrentCreatedSession(null);
        userRepository.save(session.getUser());
        session.getRegisteredUser().forEach(user -> {
            user.setCurrentParticipatedSession(null);
            userRepository.save(user);
        });
        return session;
    }

    public Session approveSession(Integer id) throws Exception {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new Exception("Session not found"));
        if (session.getStatus() != SessionStatus.PROPOSAL) throw new Exception("Only sessions in proposal status can be approved");
        session.setStatus(SessionStatus.SCHEDULED);
        return sessionRepository.save(session);
    }

    public Session rejectSession(Integer id) throws Exception {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new Exception("Session not found"));
        if (session.getStatus() != SessionStatus.PROPOSAL) throw new Exception("Only sessions in proposal status can be rejected");
        session.setStatus(SessionStatus.REJECTED);
        return sessionRepository.save(session);
    }

    public Session registerSession(Integer id, User user) throws Exception {
        Session session = findActiveSessionById(id);
        if (session.getStatus() != SessionStatus.REGISTRATION) throw new Exception("Session not in registration period");
        if (user.getCurrentParticipatedSession() != null) throw new Exception("User already participated in a session");
        session.assignSeat(user);
        user.setCurrentParticipatedSession(session);
        userRepository.save(user);
        return sessionRepository.save(session);
    }

    public Feedback giveFeedback(Integer id, FeedbackRequest request, User user) throws Exception {
        Session session = findActiveSessionById(id);
        if (session.getStatus() != SessionStatus.FINISHED) throw new Exception("Feedback only allowed after the session has finished");
        Feedback feedback = new Feedback(user, request.getContent(), request.getFeedbackRating());
        session.addFeedback(feedback);
        sessionRepository.save(session);
        return feedback;
    }

    public Feedback viewFeedback(Integer sessionId, Integer feedbackId) throws Exception {
        Session session = findActiveSessionById(sessionId);
        return session.getFeedbacks().stream()
            .filter(feedback -> feedback.getId().equals(feedbackId))
            .findFirst()
            .orElseThrow(() -> new Exception("Feedback not found"));
    }

    public Feedback deleteFeedback(Integer sessionId, Integer feedbackId) throws Exception {
        Session session = findActiveSessionById(sessionId);
        Feedback currentFeedback = session.getFeedbacks().stream()
            .filter(feedback -> feedback.getId().equals(feedbackId))
            .findFirst()
            .orElseThrow(() -> new Exception("Feedback not found"));
        session.getFeedbacks().remove(currentFeedback);
        sessionRepository.save(session);
        return currentFeedback;
    }

    @Scheduled(fixedRate = 300000)
    public void updateStatus() {
        Date currentDate = Date.from(Instant.now());

        Iterable<Session> sessions = sessionRepository.findAll();

        for (Session session : sessions) {
            if (session.getRegistrationDateStart().before(currentDate) && session.getStatus() == SessionStatus.PROPOSAL) {
                session.setStatus(SessionStatus.REJECTED);
            }
            if (session.getRegistrationDateStart().before(currentDate) && session.getStatus() == SessionStatus.SCHEDULED) {
                session.setStatus(SessionStatus.REGISTRATION);
            }
            else if (session.getSessionStart().before(currentDate) && session.getStatus() == SessionStatus.REGISTRATION) {
                session.setStatus(SessionStatus.ONGOING);
            }
            else if (
                session.getSessionStart().toInstant()
                .plusMillis(session.getSessionTime())
                .isBefore(Instant.now()) && session.getStatus() == SessionStatus.ONGOING
            ) {
                session.setStatus(SessionStatus.FINISHED);
                session.getUser().setCurrentCreatedSession(null);
                userRepository.save(session.getUser());
                session.getRegisteredUser().forEach((user) -> {
                    user.setCurrentParticipatedSession(null);
                    userRepository.save(user);
                });
            }
        }

        sessionRepository.saveAll(sessions);
    }

}
