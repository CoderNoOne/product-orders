package com.app.domain.repository;

import com.app.domain.entity.RegisterVerificationToken;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface RegisterVerificationTokenRepository extends CrudRepository<RegisterVerificationToken, Long> {
    Optional<RegisterVerificationToken> findByCustomerUsername(String username);

    Optional<RegisterVerificationToken> findByToken(String token);

    void delete(RegisterVerificationToken registerVerificationToken);

}
