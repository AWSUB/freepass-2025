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

@Service
public class SessionService {

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
        user.setCurrentCreatedSession(session);
        return sessionRepository.save(session);
    }

    public Iterable<Session> findAllSession() {
        return sessionRepository.findAll();
    }

    public Iterable<Session> findAllSessionProposal() {
        return sessionRepository.findAllByStatus(SessionStatus.PROPOSAL);
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
        return sessionRepository.findAllByUserCreator(user);
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
        session.getUserCreator().setCurrentCreatedSession(null);
        session.getRegisteredUser().forEach(user -> user.setCurrentParticipatedSession(null));
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
        user.setCurrentParticipatedSession(session);;
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
                session.getUserCreator().setCurrentCreatedSession(null);
                session.getRegisteredUser().forEach((user) -> user.setCurrentParticipatedSession(null));
            }
        }

        sessionRepository.saveAll(sessions);
    }

}
