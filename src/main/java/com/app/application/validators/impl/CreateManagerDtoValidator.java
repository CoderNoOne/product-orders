package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.UserRepository;
import com.app.infrastructure.dto.CreateManagerDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateManagerDtoValidator extends AbstractValidator<CreateManagerDto> {

    private final UserRepository userRepository;

    @Override
    public Map<String, String> validate(CreateManagerDto createManagerDto) {

        errors.clear();

        if (Objects.isNull(createManagerDto)) {
            errors.put("CreateManagerDto object", "is null");
        }

        if (Objects.isNull(createManagerDto.getEmail())) {
            errors.put("Email", "is null");
        } else if (!isEmailValid(createManagerDto.getEmail())) {
            errors.put("Email", "not valid format");
        } else if (!isEmailAvailable(createManagerDto.getEmail())) {
            errors.put("Email", "is already taken");
        }

        if (Objects.isNull(createManagerDto.getUsername())) {
            errors.put("Username", "is null");
        } else if (!isUsernameValid(createManagerDto.getUsername())) {
            errors.put("Username", " is not valid");
        } else if (!isUsernameAvailable(createManagerDto.getUsername())) {
            errors.put("Username", "is already taken");
        }

        return errors;
    }

    private boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 5;
    }

    private boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    private boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
