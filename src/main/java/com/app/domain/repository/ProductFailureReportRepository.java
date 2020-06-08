package com.app.domain.repository;

import com.app.domain.entity.ProductFailureReport;
import com.app.domain.generic.CrudRepository;

import java.util.List;

public interface ProductFailureReportRepository extends CrudRepository<ProductFailureReport, Long> {
    List<ProductFailureReport> findAllByUsername(String username);
    boolean isAnyConfirmedComplaintInProgressForProductOrderById(Long productOrderId);

    List<ProductFailureReport> findAllByManagerUsername(String username);
}
