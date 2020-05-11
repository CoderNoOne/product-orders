package com.app.infrastructure.repository.impl;

import com.app.domain.entity.RepairOrder;
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
    public List<RepairOrder> findAll() {
        return jpaRepairOrderRepository.findAll();
    }

    @Override
    public Optional<RepairOrder> findOne(Long id) {
        return jpaRepairOrderRepository.findById(id);
    }

    @Override
    public RepairOrder save(RepairOrder repairOrder) {
        return jpaRepairOrderRepository.save(repairOrder);
    }
}
