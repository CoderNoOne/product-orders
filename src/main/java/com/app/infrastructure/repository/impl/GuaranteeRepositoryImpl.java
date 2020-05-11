package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Guarantee;
import com.app.domain.repository.GuaranteeRepository;
import com.app.infrastructure.repository.jpa.JpaGuaranteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class GuaranteeRepositoryImpl implements GuaranteeRepository {

    private final JpaGuaranteeRepository jpaGuaranteeRepository;

    @Override
    public List<Guarantee> findAll() {
        return jpaGuaranteeRepository.findAll();
    }

    @Override
    public Optional<Guarantee> findOne(Long id) {
        return jpaGuaranteeRepository.findById(id);
    }

    @Override
    public Guarantee save(Guarantee guarantee) {
        return jpaGuaranteeRepository.save(guarantee);
    }

    @Override
    public Optional<Guarantee> findByName(String guaranteeName) {
        return jpaGuaranteeRepository.findByName(guaranteeName);
    }

    @Override
    public Set<Guarantee> saveAll(Set<Guarantee> guarantees) {
        return new HashSet<>(jpaGuaranteeRepository.saveAll(guarantees));
    }
}
