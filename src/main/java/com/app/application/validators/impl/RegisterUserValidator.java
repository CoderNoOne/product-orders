package com.app.application.validators.impl;

import com.app.application.dto.RegisterUserDto;
import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.RoleRepository;
import com.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class RegisterUserValidator extends AbstractValidator<RegisterUserDto> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final List<String> allowedRoles;

    @Override
    public Map<String, String> validate(RegisterUserDto registerUserDto) {
        errors.clear();

        if (registerUserDto == null) {
            errors.put("dto object", "is null");
            return errors;
        }

        if (!validateUsername(registerUserDto.getUsername())) {
            errors.put("username", "is not correct");
        }

        if (!validateEmail(registerUserDto.getEmail())) {
            errors.put("email", "email is not correct");
        }

        if (!validateRole(registerUserDto.getRole())) {
            errors.put("role", "role is not correct");
        }

        if (!validatePasswords(registerUserDto.getPassword(), registerUserDto.getPasswordConfirmation())) {
            errors.put("password", "password is not correct");
        }

        if (!validateAge(registerUserDto.getRole(), registerUserDto.getAge())) {
            errors.put("Age", "Age is not correct");

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

    private boolean validateRole(String name) {
        return Objects.nonNull(name) && roleRepository.findByName("ROLE_" + name).isPresent()
                && allowedRoles.contains("ROLE_" + name);
    }

    private boolean validatePasswords(String password, String passwordConfirmation) {
        return Objects.equals(password, passwordConfirmation);
    }

    private boolean validateAge(String roleName, Integer age) {
        return !Objects.equals(roleName, "USER_CUSTOMER") ||
                Objects.nonNull(age) && age >= 18;
    }
}
