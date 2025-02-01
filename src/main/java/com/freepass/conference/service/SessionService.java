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

import jakarta.transaction.Transactional;

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

    public Session findSessionById(Integer id) throws Exception {
        return sessionRepository.findById(id).orElseThrow(() -> new Exception("Session not found"));
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
        for (Session session : findAllSessionProposal()) {
            if (session.getId() == id) return session;
        }
        throw new Exception("Session proposal not found");
    }

    public Iterable<Session> findSessionByUser(User user) {
        return sessionRepository.findByUser(user);
    }

    public Session updateSession(SessionRequest request, Session session) throws Exception {
        Session currentSession = sessionRepository.findById(session.getId()).orElseThrow(() -> new Exception("Session not found"));
        if (currentSession.getStatus() == SessionStatus.PROPOSAL) {
            currentSession.setTitle(request.getTitle());
            currentSession.setDescription(request.getDescription());
            currentSession.setRegistrationDateStart(request.getRegistrationDateStart());
            currentSession.setSessionStart(request.getSessionStart());
            currentSession.setSessionTime(request.getSessionTime());
        }
        else if (currentSession.getStatus() != SessionStatus.FINISHED) {
            currentSession.setTitle(request.getTitle());
            currentSession.setDescription(request.getDescription());
        }
        else throw new Exception("Finished session cannot be updated");
        return sessionRepository.save(currentSession);
    }

    public Session removeSession(Session session) throws Exception {
        Session currentSession = findActiveSessionById(session.getId());
        if (currentSession.getStatus() == SessionStatus.FINISHED) {
            throw new Exception("Finished session cannot be removed");
        }
        sessionRepository.delete(currentSession);
        currentSession.getUser().setCurrentCreatedSession(null);
        userRepository.save(currentSession.getUser());
        currentSession.getRegisteredUser().forEach(user -> {
            user.setCurrentParticipatedSession(null);
            userRepository.save(user);
        });
        return currentSession;
    }

    public Session approveSession(Integer id) throws Exception {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new Exception("Session not found"));
        if (session.getStatus() != SessionStatus.PROPOSAL) throw new Exception("Only sessions in proposal status can be approved");
        else if (session.getSessionStart().before(Date.from(Instant.now()))) {
            session.getUser().getCurrentCreatedSession().setStatus(SessionStatus.REJECTED);
            userRepository.save(session.getUser());
            sessionRepository.save(session);
            throw new Exception("Session proposal expired");
        }
        session.setStatus(SessionStatus.SCHEDULED);
        session.getUser().getCurrentCreatedSession().setStatus(SessionStatus.SCHEDULED);
        userRepository.save(session.getUser());
        return sessionRepository.save(session);
    }

    public Session rejectSession(Integer id) throws Exception {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new Exception("Session not found"));
        // if (session.getStatus() != SessionStatus.PROPOSAL) throw new Exception("Only sessions in proposal status can be rejected");
        session.setStatus(SessionStatus.REJECTED);
        session.getUser().getCurrentCreatedSession().setStatus(SessionStatus.REJECTED);
        userRepository.save(session.getUser());
        return sessionRepository.save(session);
    }

    @Transactional
    public Session registerSession(Integer id, User user) throws Exception {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new Exception("Active session not found"));
        User currentUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("User not found"));
        // if (session.getStatus() != SessionStatus.REGISTRATION) throw new Exception("Session not in registration period");
        // if (currentUser.getCurrentParticipatedSession() != null) throw new Exception("User already participated in a session");
        session.assignSeat(currentUser);
        currentUser.setCurrentParticipatedSession(session);
        sessionRepository.save(session);
        userRepository.save(currentUser);
        return session;
    }

    @Transactional
    public Feedback giveFeedback(Integer id, FeedbackRequest request, User user) throws Exception {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new Exception("Active session not found"));
        User currentUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("User not found"));
        // if (currentUser.getCurrentParticipatedSession().getId() != session.getId()) throw new Exception("Feedback can only be given by registered user");
        // if (session.getStatus() != SessionStatus.FINISHED) throw new Exception("Feedback only allowed after the session has finished");
        Feedback feedback = new Feedback(currentUser, request.getContent(), request.getFeedbackRating());
        session.addFeedback(feedback);
        sessionRepository.save(session);
        userRepository.save(currentUser);
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
    public void updateStatus() throws Exception {
        Date currentDate = Date.from(Instant.now());

        Iterable<Session> sessions = sessionRepository.findAll();

        for (Session session : sessions) {
            if (session.getRegistrationDateStart().before(currentDate) && session.getStatus() == SessionStatus.SCHEDULED) {
                session.setStatus(SessionStatus.REGISTRATION);
                session.getUser().getCurrentCreatedSession().setStatus(SessionStatus.REGISTRATION);
                userRepository.save(session.getUser());
            }
            if (session.getSessionStart().before(currentDate) && session.getStatus() == SessionStatus.PROPOSAL) {
                rejectSession(session.getId());
            }
            else if (session.getSessionStart().before(currentDate) && session.getStatus() == SessionStatus.REGISTRATION) {
                session.setStatus(SessionStatus.ONGOING);
                session.getUser().getCurrentCreatedSession().setStatus(SessionStatus.ONGOING);
                userRepository.save(session.getUser());                
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
