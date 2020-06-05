package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductFailureOnGuaranteeReport;
import com.app.domain.repository.ProductFailureOnGuaranteeReportRepository;
import com.app.infrastructure.repository.jpa.JpaProductFailureOnGuaranteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductFailureOnGuaranteeReportRepositoryImpl implements ProductFailureOnGuaranteeReportRepository {

    private final JpaProductFailureOnGuaranteeRepository jpaProductFailureOnGuaranteeRepository;

    @Override
    public List<ProductFailureOnGuaranteeReport> findAll() {
        return jpaProductFailureOnGuaranteeRepository.findAll();
    }

    @Override
    public Optional<ProductFailureOnGuaranteeReport> findOne(Long id) {
        return jpaProductFailureOnGuaranteeRepository.findById(id);
    }

    @Override
    public ProductFailureOnGuaranteeReport save(ProductFailureOnGuaranteeReport productFailureOnGuaranteeReport) {
        return jpaProductFailureOnGuaranteeRepository.save(productFailureOnGuaranteeReport);
    }
}
