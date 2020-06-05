package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureOnGuaranteeReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductFailureOnGuaranteeReportRepository extends JpaRepository<ProductFailureOnGuaranteeReport, Long> {
}
