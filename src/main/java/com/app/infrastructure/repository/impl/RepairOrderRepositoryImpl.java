package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductFailureReport;
import com.app.domain.repository.RepairOrderRepository;
import com.app.infrastructure.repository.jpa.JpaRepairOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RepairOrderRepositoryImpl implements RepairOrderRepository {

    private final JpaRepairOrderRepository jpaRepairOrderRepository;

    @Override
    public List<ProductFailureReport> findAll() {
        return jpaRepairOrderRepository.findAll();
    }

    @Override
    public Optional<ProductFailureReport> findOne(Long id) {
        return jpaRepairOrderRepository.findById(id);
    }

    @Override
    public ProductFailureReport save(ProductFailureReport repairOrder) {
        return jpaRepairOrderRepository.save(repairOrder);
    }

    @Override
    public List<ProductFailureReport> findAllByCustomerUsername(String username) {
        return jpaRepairOrderRepository.findAllByProductOrderCustomerUsername(username);
    }

    @Override
    public List<ProductFailureReport> findAllByManagerUsername(String username) {
        return jpaRepairOrderRepository.findAllByProductOrderCustomerManagerUsername(username);
    }

    @Override
    public Optional<ProductFailureReport> findByIdAndCustomerUsername(Long id, String username) {
        return jpaRepairOrderRepository.findByIdAndProductOrderCustomerUsername(id, username);
    }

    @Override
    public Optional<ProductFailureReport> findByIdAndManagerUsername(Long id, String username) {
        return jpaRepairOrderRepository.findByIdAndProductOrderCustomerManagerUsername(id, username);
    }
}
