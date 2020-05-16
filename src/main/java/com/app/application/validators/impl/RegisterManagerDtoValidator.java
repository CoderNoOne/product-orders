package com.app.application.validators.impl;

import com.app.application.dto.RegisterManagerDto;
import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class RegisterManagerDtoValidator extends AbstractValidator<RegisterManagerDto> {

    private final UserRepository userRepository;

    @Override
    public Map<String, String> validate(RegisterManagerDto registerManagerDto) {

        errors.clear();

        if (Objects.isNull(registerManagerDto)) {
            errors.put("RegisterManagerDto object", "is null");
            return errors;
        }

        if (!validateUsername(registerManagerDto.getUsername())) {
            errors.put("username", "is not correct. Length 5-30 is allowed");
        } else if (!isUsernameAvailable(registerManagerDto.getUsername())) {
            errors.put("username", "is already taken");
        }

        if (!validateEmail(registerManagerDto.getEmail())) {
            errors.put("email", "email is not correct");
        } else if (!isEmailAvailable(registerManagerDto.getEmail())) {
            errors.put("email", "is already taken");
        }

        if (!validatePasswords(registerManagerDto.getPassword(), registerManagerDto.getPasswordConfirmation())) {
            errors.put("password", "password is not correct");
        }


        return errors;

    }

    private boolean validateEmail(String email) {
        return
                Objects.nonNull(email) && EmailValidator.getInstance().isValid(email) &&
                        userRepository.findByEmail(email).isEmpty();
    }

    private boolean validateUsername(String username) {
        return Objects.nonNull(username) &&
                userRepository.findByUsername(username).isEmpty();
    }

    private boolean validatePasswords(String password, String passwordConfirmation) {
        return Objects.equals(password, passwordConfirmation);
    }

    private boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

}
