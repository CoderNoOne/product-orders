package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Producer;
import com.app.domain.repository.ProducerRepository;
import com.app.infrastructure.repository.jpa.JpaProducerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProducerRepositoryImpl implements ProducerRepository {

    private final JpaProducerRepository jpaProducerRepository;

    @Override
    public List<Producer> findAll() {
        return jpaProducerRepository.findAll();
    }

    @Override
    public Optional<Producer> findOne(Long id) {
        return jpaProducerRepository.findById(id);
    }

    @Override
    public Producer save(Producer producer) {
        return jpaProducerRepository.save(producer);
    }

    @Override
    public Optional<Producer> findByName(String name) {
        return jpaProducerRepository.findByName(name);
    }

    @Override
    public Optional<Producer> findByNameWithFetchedGuarantees(String name) {
        return jpaProducerRepository.findByNameIs(name);
    }
}
