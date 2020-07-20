package com.app.domain.repository;

import com.app.domain.entity.User;
import com.app.domain.generic.CrudRepository;
import com.app.infrastructure.dto.projection.UserEmail;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "select u.email from User u where u.username = :username")
    Optional<String> findEmailByUsername(String username);

    Optional<UserEmail> findEmailById(Long id);

}
