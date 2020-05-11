package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.User;
import com.app.infrastructure.dto.projection.UserEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<UserEmail> findEmailByUsername(String username);

}
