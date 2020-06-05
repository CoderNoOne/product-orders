package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureOnGuaranteeReport;
import com.app.domain.entity.ProductFailureReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductFailureOnGuaranteeRepository extends JpaRepository<ProductFailureOnGuaranteeReport, Long> {
}
