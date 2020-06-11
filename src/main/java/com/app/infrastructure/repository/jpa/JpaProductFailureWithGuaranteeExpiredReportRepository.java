package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaProductFailureWithGuaranteeExpiredReportRepository extends JpaRepository <ProductFailureWithGuaranteeExpiredReport, Long> {
    @Query("select p from ProductFailureWithGuaranteeExpiredReport p where p.id = :id and p.productOrder.customer.manager.username = :username")
    Optional<ProductFailureWithGuaranteeExpiredReport> findByIdAndManagerUsername(Long id, String username);
}
