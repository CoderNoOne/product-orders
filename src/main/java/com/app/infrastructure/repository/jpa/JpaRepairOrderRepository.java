package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRepairOrderRepository extends JpaRepository <ProductFailureReport, Long> {

    List<ProductFailureReport> findAllByProductOrderCustomerUsername(String username);

    List<ProductFailureReport> findAllByProductOrderCustomerManagerUsername(String username);

    Optional<ProductFailureReport> findByIdAndProductOrderCustomerUsername(Long id, String username);

    Optional<ProductFailureReport> findByIdAndProductOrderCustomerManagerUsername(Long id, String username);
}
