package com.app.application.service;

import com.app.domain.entity.RegisterVerificationToken;
import com.app.domain.repository.RegisterVerificationTokenRepository;
import com.fasterxml.jackson.databind.deser.std.AtomicBooleanDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterVerificationTokenService {

    private final RegisterVerificationTokenRepository registerVerificationTokenRepository;

    public Long save(String username) {

        String token = UUID.randomUUID().toString();

        var idWrapper = new AtomicLong();
        registerVerificationTokenRepository.findByCustomerUsername(username)
                .ifPresentOrElse(
                        tokenFromDb -> {
                            tokenFromDb
                                    .token(token)
                                    .expirationTime(LocalDateTime.now().plusHours(24));
                            idWrapper.set(tokenFromDb.getId());
                        },
                        () -> idWrapper.set(registerVerificationTokenRepository.save(RegisterVerificationToken
                                .builder()
                                .token(token)
                                .expirationTime(LocalDateTime.now().plusHours(24))
                                .build()).getId()
                        ));

        return idWrapper.get();
    }
}
