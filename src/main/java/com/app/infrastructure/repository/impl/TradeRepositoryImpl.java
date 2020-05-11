package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Trade;
import com.app.domain.repository.TradeRepository;
import com.app.infrastructure.repository.jpa.JpaTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TradeRepositoryImpl implements TradeRepository {

    private final JpaTradeRepository jpaTradeRepository;

    @Override
    public Optional<Trade> findByName(String name) {
        return jpaTradeRepository.findByName(name);
    }

    @Override
    public List<Trade> findAll() {
        return jpaTradeRepository.findAll();
    }

    @Override
    public Optional<Trade> findOne(Long id) {
        return jpaTradeRepository.findById(id);
    }

    @Override
    public Trade save(Trade trade) {
        return jpaTradeRepository.save(trade);
    }
}
