package com.app.application.service;

import com.app.application.dto.RegisterCustomerDto;
import com.app.application.dto.RegisterManagerDto;
import com.app.application.dto.RegisterUserDto;
import com.app.application.exception.RegisterUserException;
import com.app.application.mappers.Mappers;
import com.app.application.validators.impl.RegisterCustomerDtoValidator;
import com.app.application.validators.impl.RegisterManagerDtoValidator;
import com.app.application.validators.impl.RegisterUserValidator;
import com.app.domain.entity.Customer;
import com.app.domain.entity.Role;
import com.app.domain.repository.ManagerRepository;
import com.app.domain.repository.RoleRepository;
import com.app.domain.entity.User;
import com.app.domain.repository.UserRepository;
import com.app.infrastructure.exception.ValidationException;
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
    private final RegisterCustomerDtoValidator registerCustomerDtoValidator;
    private final RegisterManagerDtoValidator registerManagerDtoValidator;
    private final PasswordEncoder passwordEncoder;
    private final ManagerRepository managerRepository;

    public Long registerCustomer(RegisterCustomerDto registerCustomerDto) {

        var errors = registerCustomerDtoValidator.validate(registerCustomerDto);

        if (registerCustomerDtoValidator.hasErrors()) {
            throw new RegisterUserException(Validations.createErrorMessage(errors));
        }

        var customer = registerCustomerDto.toEntity();

        managerRepository.findOneWithLeastCustomers()
                .ifPresentOrElse(
                        manager -> {
                            Objects.requireNonNull(customer).setManager(manager);
                            manager.getCustomers().add(customer);
                        }
                        , () -> {
                            throw new ValidationException("Registration is not available now. Waiting for more managers");
                        }
                );

        return saveUser(customer);

    }

    public Long registerManager(RegisterManagerDto registerManagerDto) {

        var errors = registerManagerDtoValidator.validate(registerManagerDto);

        if (registerManagerDtoValidator.hasErrors()) {
            throw new RegisterUserException(Validations.createErrorMessage(errors));
        }

        return saveUser(registerManagerDto.toEntity());

    }

    private Long saveUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        roleRepository.findByName(user.getRole().getName()).ifPresent(user::setRole);

        return userRepository.save(user).getId();
    }
}
