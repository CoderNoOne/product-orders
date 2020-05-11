package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByName(String name);
}
