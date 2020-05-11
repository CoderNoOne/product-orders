package com.app.infrastructure.repository.impl;

import com.app.domain.entity.User;
import com.app.domain.repository.UserRepository;
import com.app.infrastructure.dto.projection.UserEmail;
import com.app.infrastructure.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public Optional<User> findOne(Long aLong) {
        return jpaUserRepository.findById(aLong);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public Optional<String> findEmailByUsername(String username) {
        return jpaUserRepository.findEmailByUsername(username).map(UserEmail::getEmail);
    }
}
