package com.app.domain.repository;

import com.app.domain.entity.ProductFailureReport;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository extends CrudRepository<ProductFailureReport, Long> {
    List<ProductFailureReport> findAllByCustomerUsername(String username);

    List<ProductFailureReport> findAllByManagerUsername(String username);

    Optional<ProductFailureReport> findByIdAndCustomerUsername(Long id, String username);

    Optional<ProductFailureReport> findByIdAndManagerUsername(Long id, String username);
}
