package com.app.application.service;

import com.app.domain.entity.Manager;
import com.app.domain.entity.User;
import com.app.domain.repository.UserRepository;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullReferenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String getEmailForUsername(String username) {

        if (Objects.isNull(username)) {
            throw new NullReferenceException("Username is null");
        }

        return userRepository.findEmailByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user with username: " + username));

    }

    public User getUserForUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user with username: " + username));

    }

    public boolean isManager(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user with username: " + username))
                instanceof Manager;
    }
}
