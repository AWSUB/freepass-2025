package com.freepass.conference.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.freepass.conference.enums.SessionStatus;
import com.freepass.conference.model.Session;
import com.freepass.conference.model.User;

@Repository
public interface SessionRepository extends CrudRepository<Session, Integer> {
    public Iterable<Session> findAllByUserCreator(User userCreator);
    public Iterable<Session> findAllByStatus(SessionStatus status);

    @Query(value = "SELECT * FROM session WHERE session.status != :status", nativeQuery = true)
    public Iterable<Session> findByStatusExcept(SessionStatus status);
}
