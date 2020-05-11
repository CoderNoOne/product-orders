package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRepairOrderRepository extends JpaRepository <RepairOrder, Long> {
}
