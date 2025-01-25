package com.freepass.conference.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.freepass.conference.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {  
    public Optional<User> findByUsername(String username);
    public Optional<User> findByEmail(String email);
}
