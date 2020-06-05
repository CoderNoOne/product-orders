package com.app.domain.repository;

import com.app.domain.entity.ProductFailureOnGuaranteeReport;
import com.app.domain.entity.ProductFailureReport;
import com.app.domain.generic.CrudRepository;

import java.util.List;

public interface ProductFailureReportRepository extends CrudRepository<ProductFailureReport, Long> {
    List<ProductFailureReport> findAllByUsername(String username);
}
