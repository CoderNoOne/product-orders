package com.app.domain.repository;

import com.app.domain.entity.User;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<String> findEmailByUsername(String username);
}
