package com.app.application.service;

import com.app.domain.entity.Producer;
import com.app.domain.repository.ProducerRepository;
import com.app.infrastructure.dto.ProducerDto;
import com.app.infrastructure.exception.NullReferenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProducerService {

    private final ProducerRepository producerRepository;

    public List<ProducerDto> getAllProducers() {
        return producerRepository.findAll()
                .stream()
                .map(Producer::toDto)
                .collect(Collectors.toList());
    }

    public List<ProducerDto> getProducersByTrade(String trade) {

        if (Objects.isNull(trade)) {
            throw new NullReferenceException("Trade is  null");
        }
        return producerRepository.findAllByTrade(trade)
                .stream()
                .map(Producer::toDto)
                .collect(Collectors.toList());
    }
}
