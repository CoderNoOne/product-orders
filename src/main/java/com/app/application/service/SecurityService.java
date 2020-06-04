package com.app.application.service;

import com.app.application.dto.RegisterCustomerDto;
import com.app.application.dto.RegisterManagerDto;
import com.app.application.exception.RegisterUserException;
import com.app.application.validators.impl.RegisterCustomerDtoValidator;
import com.app.application.validators.impl.RegisterManagerDtoValidator;
import com.app.domain.entity.Customer;
import com.app.domain.entity.RegisterVerificationToken;
import com.app.domain.entity.User;
import com.app.domain.enums.Gender;
import com.app.domain.repository.*;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

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
    private final CustomerRepository customerRepository;
    private final RegisterVerificationTokenRepository registerVerificationTokenRepository;

    public Long registerCustomer(RegisterCustomerDto registerCustomerDto) {

        var errors = registerCustomerDtoValidator.validate(registerCustomerDto);

        if (registerCustomerDtoValidator.hasErrors()) {
            throw new RegisterUserException(Validations.createErrorMessage(errors));
        }

        var customer = registerCustomerDto.toEntity();

        var idWrapper = new AtomicLong();

        managerRepository.findOneWithLeastCustomers()
                .ifPresentOrElse(
                        manager -> {
                            Objects.requireNonNull(customer).setManager(manager);
                            Customer savedCustomer = (Customer) saveUser(customer);
                            manager.getCustomers().add(savedCustomer);
                            customer.setGender(Gender.valueOf(registerCustomerDto.getGender()));
                            registerVerificationTokenRepository.save(RegisterVerificationToken.builder()
                                    .expirationTime(LocalDateTime.now().plusHours(24))
                                    .token(UUID.randomUUID().toString())
                                    .customer(savedCustomer)
                                    .build());
                            idWrapper.set(savedCustomer.getId());
                        }
                        , () -> {
                            throw new ValidationException("Registration is not available now. Waiting for more managers");
                        }
                );

        return idWrapper.get();

    }

    public Long registerManager(RegisterManagerDto registerManagerDto) {

        var errors = registerManagerDtoValidator.validate(registerManagerDto);

        if (registerManagerDtoValidator.hasErrors()) {
            throw new RegisterUserException(Validations.createErrorMessage(errors));
        }

        return saveUser(registerManagerDto.toEntity()).getId();

    }

    private User saveUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        roleRepository.findByName(user.getRole().getName()).ifPresent(user::setRole);

        return userRepository.save(user);
    }

    public Long activateCustomer(String token) {

        var idWrapper = new AtomicLong();
        registerVerificationTokenRepository.findByToken(token)
                .ifPresentOrElse(
                        tokenFromDb -> {
                            if (tokenFromDb.getExpirationTime().compareTo(LocalDateTime.now()) < 0) {
                                throw new ValidationException("Token had already expired");
                            }

                            Customer customer = tokenFromDb.getCustomer();
                            idWrapper.set(customer.getId());
                            customer.setEnabled(true);

                            registerVerificationTokenRepository.delete(tokenFromDb);

                        }, () -> {
                            throw new NotFoundException("Not valid token");
                        }
                );
        return idWrapper.get();
    }
}
