package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.RegisterVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRegisterVerificationTokenRepository extends JpaRepository<RegisterVerificationToken, Long> {
    Optional<RegisterVerificationToken> findByCustomerUsername(String username);

    Optional<RegisterVerificationToken> findByToken(String token);
}
