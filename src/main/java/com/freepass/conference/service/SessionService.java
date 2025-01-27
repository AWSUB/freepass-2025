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
        if (!isSessionRequestValid(request)) throw new Exception("Request not valid");
        if (user.hasSession()) throw new Exception("User already proposed a session or has an active session");
        user.setHasSession(true);
        return sessionRepository.save(new Session(
            request.getTitle(),
            request.getDescription(), 
            request.getSeatsAvailable(), 
            request.getRegistrationDateStart(), 
            request.getSessionStart(), 
            request.getSessionTime(), 
            user
        ));
    }

    public boolean isSessionRequestValid(SessionRequest request) {
        if (request.getSeatsAvailable() <= 0) return false;
        if (request.getRegistrationDateStart().after(request.getSessionStart())) return false;
        if (request.getSessionTime() <= 0) return false;
        return true;
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

    public Session findSessionById(Integer id) throws Exception{
        return sessionRepository.findById(id).orElseThrow(() -> new Exception("Session not found"));
    }

    public Session approveSession(Integer id) {
        Session session = sessionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setStatus(SessionStatus.SCHEDULED);
        return sessionRepository.save(session);
    }

    public Session registerSession(Integer id, User user) throws Exception {
        Session session = findSessionById(id);
        if (user.isSessionParticipant()) throw new Exception("User already participated in a session");
        session.assignSeat(user);
        user.setIsSessionParticipant(true);
        return sessionRepository.save(session);
    }

    public Feedback giveFeedback(Integer id, FeedbackRequest request, User user) throws Exception {
        Session session = findSessionById(id);
        Feedback feedback = new Feedback(user, request.getContent());
        session.addFeedback(feedback);
        sessionRepository.save(session);
        return feedback;
    }

    @Scheduled(fixedRate = 3600000)
    public void updateStatus() {
        Date currentDate = Date.from(Instant.now());

        Iterable<Session> sessions = sessionRepository.findAll();

        for (Session session : sessions) {
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
                session.getUserCreator().setHasSession(false);
                session.getRegisteredUser().forEach((user) -> user.setIsSessionParticipant(false));
            }
        }

        sessionRepository.saveAll(sessions);
    }

}
