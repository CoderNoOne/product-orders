package com.app.application.validators.impl;

import com.app.application.dto.RegisterCustomerDto;
import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.Gender;
import com.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class RegisterCustomerDtoValidator extends AbstractValidator<RegisterCustomerDto> {

    private final UserRepository userRepository;

    @Override
    public Map<String, String> validate(RegisterCustomerDto registerCustomerDto) {

        errors.clear();

        if (Objects.isNull(registerCustomerDto)) {
            errors.put("RegisterCustomerDto object", "is null");
            return errors;
        }

        if (!validateUsername(registerCustomerDto.getUsername())) {
            errors.put("username", "is not correct. Length 5-30 is allowed");
        } else if (!isUsernameAvailable(registerCustomerDto.getUsername())) {
            errors.put("username", "is already taken");
        }

        if (!validateEmail(registerCustomerDto.getEmail())) {
            errors.put("email", "email is not correct");
        } else if (!isEmailAvailable(registerCustomerDto.getEmail())) {
            errors.put("email", "is already taken");
        }

        if (!validatePasswords(registerCustomerDto.getPassword(), registerCustomerDto.getPasswordConfirmation())) {
            errors.put("password", "password is not correct");
        }

        if (!validateAge(registerCustomerDto.getAge())) {
            errors.put("Age", "Age is not correct. Have to be an adult");
        }


        if (!validateGender(registerCustomerDto.getGender())) {
            errors.put("Gender", "Gender accepted values are: male, female");
        }

        return errors;
    }

    private boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    private boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    private boolean validateGender(String gender) {
        return Objects.nonNull(gender) && Arrays.stream(Gender.values()).map(Gender::name)
                .anyMatch(genderVal -> genderVal.equals(gender));
    }

    private boolean validateEmail(String email) {
        return Objects.nonNull(email) && EmailValidator.getInstance().isValid(email);
    }

    private boolean validateUsername(String username) {
        return Objects.nonNull(username) && username.length() >= 5 && username.length() <= 30;

    }

    private boolean validatePasswords(String password, String passwordConfirmation) {
        return Objects.equals(password, passwordConfirmation);
    }

    private boolean validateAge(Integer age) {
        return Objects.nonNull(age) && age >= 18;
    }
}
