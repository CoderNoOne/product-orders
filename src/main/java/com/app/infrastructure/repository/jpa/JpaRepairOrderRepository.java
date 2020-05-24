package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.RepairOrder;
import com.app.infrastructure.dto.RepairOrderDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRepairOrderRepository extends JpaRepository <RepairOrder, Long> {

    List<RepairOrder> findAllByProductOrderCustomerUsername(String username);

    List<RepairOrder> findAllByProductOrderCustomerManagerUsername(String username);

    Optional<RepairOrder> findByIdAndProductOrderCustomerUsername(Long id, String username);

    Optional<RepairOrder> findByIdAndProductOrderCustomerManagerUsername(Long id, String username);
}
