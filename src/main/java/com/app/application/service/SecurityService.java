package com.app.application.service;

import com.app.application.dto.RegisterUserDto;
import com.app.application.exception.RegisterUserException;
import com.app.application.mappers.Mappers;
import com.app.application.validators.impl.RegisterUserValidator;
import com.app.domain.entity.Role;
import com.app.domain.repository.RoleRepository;
import com.app.domain.entity.User;
import com.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RegisterUserValidator registerUserValidator;
    private final PasswordEncoder passwordEncoder;

    public Long register(RegisterUserDto registerUserDto) {

        var errors = registerUserValidator.validate(registerUserDto);

        if (registerUserValidator.hasErrors()) {
            throw new RegisterUserException(Validations.createErrorMessage(errors));
        }

        Role role = roleRepository.findByName("ROLE_" + registerUserDto.getRole())
                .orElseThrow(() ->
                        new RegisterUserException(Validations.createErrorMessage(
                                Map.of("Role", "Role is incorrect"))));

        User user = Mappers.fromDtoToEntity(registerUserDto);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(
                Objects.equals(role.getName(), "ROLE_USER_CUSTOMER") ?
                        Mappers.fromUserToCustomer(user, registerUserDto.getAge())
                        : Mappers.fromUserToManager(user)).getId();

    }

}
