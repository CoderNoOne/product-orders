package com.app.domain.repository;

import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface ProductFailureWithGuaranteeExpiredReportRepository extends CrudRepository<ProductFailureWithGuaranteeExpiredReport, Long> {
    Optional<ProductFailureWithGuaranteeExpiredReport> findById(Long id);

    Optional<ProductFailureWithGuaranteeExpiredReport> findByIdAndManagerUsername(Long id, String managerUsername);

    Optional<ProductFailureWithGuaranteeExpiredReport> findByIdAndCustomerUsername(Long productFailureWithGuaranteeExpiredReportId, String customerUsername);
}
