package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductFailureWithGuaranteeExpiredReportRepository extends JpaRepository <ProductFailureWithGuaranteeExpiredReport, Long> {
}
