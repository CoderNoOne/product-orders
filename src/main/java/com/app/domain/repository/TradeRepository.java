package com.app.domain.repository;

import com.app.domain.entity.Trade;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface TradeRepository extends CrudRepository <Trade, Long> {
    Optional<Trade> findByName(String name);
}
