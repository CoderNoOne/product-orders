package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Manager;
import com.app.domain.repository.ManagerRepository;
import com.app.infrastructure.repository.jpa.JpaManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ManagerRepositoryImpl implements ManagerRepository {

    private final JpaManagerRepository jpaManagerRepository;

    @Override
    public Optional<Manager> findOneWithLeastCustomers() {
        List<Manager> managers = jpaManagerRepository.findManagersOrderByCustomersNumber();

        return managers.isEmpty() ? Optional.empty() :
                Optional.of(managers.get(0));
    }

    @Override
    public List<Manager> findAll() {
        return jpaManagerRepository.findAll();
    }

    @Override
    public Optional<Manager> findOne(Long id) {
        return jpaManagerRepository.findById(id);
    }

    @Override
    public Manager save(Manager manager) {
        return jpaManagerRepository.save(manager);
    }
}
