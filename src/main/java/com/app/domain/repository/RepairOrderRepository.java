package com.app.domain.repository;

import com.app.domain.entity.RepairOrder;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository extends CrudRepository<RepairOrder, Long> {
    List<RepairOrder> findAllByCustomerUsername(String username);

    List<RepairOrder> findAllByManagerUsername(String username);

    Optional<RepairOrder> findByIdAndCustomerUsername(Long id, String username);

    Optional<RepairOrder> findByIdAndManagerUsername(Long id, String username);
}
