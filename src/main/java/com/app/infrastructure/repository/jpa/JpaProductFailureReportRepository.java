package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductFailureReportRepository extends JpaRepository<ProductFailureReport, Long> {
    List<ProductFailureReport> findAllByProductOrderCustomerUsername(String username);
}
