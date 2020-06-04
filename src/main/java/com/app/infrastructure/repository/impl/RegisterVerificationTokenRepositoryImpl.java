package com.app.infrastructure.repository.impl;

import com.app.domain.entity.RegisterVerificationToken;
import com.app.domain.repository.RegisterVerificationTokenRepository;
import com.app.infrastructure.repository.jpa.JpaRegisterVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegisterVerificationTokenRepositoryImpl implements RegisterVerificationTokenRepository {

    private final JpaRegisterVerificationTokenRepository jpaRegisterVerificationTokenRepository;

    @Override
    public List<RegisterVerificationToken> findAll() {
        return jpaRegisterVerificationTokenRepository.findAll();
    }

    @Override
    public Optional<RegisterVerificationToken> findOne(Long id) {
        return jpaRegisterVerificationTokenRepository.findById(id);
    }

    @Override
    public RegisterVerificationToken save(RegisterVerificationToken registerVerificationToken) {
        return jpaRegisterVerificationTokenRepository.save(registerVerificationToken);
    }

    @Override
    public Optional<RegisterVerificationToken> findByCustomerUsername(String username) {
        return jpaRegisterVerificationTokenRepository.findByCustomerUsername(username);
    }

    @Override
    public Optional<RegisterVerificationToken> findByToken(String token) {
        return jpaRegisterVerificationTokenRepository.findByToken(token);
    }

    @Override
    public void delete(RegisterVerificationToken registerVerificationToken) {
        jpaRegisterVerificationTokenRepository.delete(registerVerificationToken);
    }
}
