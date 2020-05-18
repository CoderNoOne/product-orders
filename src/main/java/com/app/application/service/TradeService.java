package com.app.application.service;

import com.app.domain.entity.Trade;
import com.app.domain.repository.TradeRepository;
import com.app.infrastructure.dto.TradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public List<TradeDto> getAllTrades(){
        return tradeRepository.findAll()
                .stream()
                .map(Trade::toDto)
                .collect(Collectors.toList());
    }
}
