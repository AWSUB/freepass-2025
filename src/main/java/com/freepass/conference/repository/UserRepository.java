package com.freepass.conference.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freepass.conference.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {  
    public Optional<User> finfindByUsername(String username);
    public Optional<User> findByEmail(String email);
}
