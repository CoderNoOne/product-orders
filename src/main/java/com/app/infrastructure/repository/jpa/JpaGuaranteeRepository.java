package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Guarantee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaGuaranteeRepository extends JpaRepository<Guarantee, Long> {
    Optional<Guarantee> findByName(String guaranteeName);
}
