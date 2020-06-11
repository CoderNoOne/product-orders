package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import com.app.domain.repository.ProductFailureWithGuaranteeExpiredReportRepository;
import com.app.infrastructure.repository.jpa.JpaProductFailureWithGuaranteeExpiredReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductFailureWithGuaranteeExpiredReportRepositoryImpl implements ProductFailureWithGuaranteeExpiredReportRepository {

    private final JpaProductFailureWithGuaranteeExpiredReportRepository jpaProductFailureWithGuaranteeExpiredReportRepository;

    @Override
    public List<ProductFailureWithGuaranteeExpiredReport> findAll() {
        return jpaProductFailureWithGuaranteeExpiredReportRepository.findAll();
    }

    @Override
    public Optional<ProductFailureWithGuaranteeExpiredReport> findOne(Long id) {
        return jpaProductFailureWithGuaranteeExpiredReportRepository.findById(id);
    }

    @Override
    public ProductFailureWithGuaranteeExpiredReport save(ProductFailureWithGuaranteeExpiredReport productFailureWithGuaranteeExpiredReport) {
        return jpaProductFailureWithGuaranteeExpiredReportRepository.save(productFailureWithGuaranteeExpiredReport);
    }

    @Override
    public Optional<ProductFailureWithGuaranteeExpiredReport> findById(Long id) {
        return jpaProductFailureWithGuaranteeExpiredReportRepository.findById(id);
    }

    @Override
    public Optional<ProductFailureWithGuaranteeExpiredReport> findByIdAndManagerUsername(Long id, String managerUsername) {
        return jpaProductFailureWithGuaranteeExpiredReportRepository.findByIdAndManagerUsername(id, managerUsername);
    }
}
