package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductFailureReport;
import com.app.domain.repository.ProductFailureReportRepository;
import com.app.infrastructure.repository.jpa.JpaProductFailureReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductFailureReportRepositoryImpl implements ProductFailureReportRepository {

    private final JpaProductFailureReportRepository jpaProductFailureReportRepository;

    @Override
    public List<ProductFailureReport> findAllByUsername(String username) {
        return jpaProductFailureReportRepository.findAllByProductOrderCustomerUsername(username);
    }

    @Override
    public List<ProductFailureReport> findAll() {
        return jpaProductFailureReportRepository.findAll();
    }

    @Override
    public Optional<ProductFailureReport> findOne(Long id) {
        return jpaProductFailureReportRepository.findById(id);
    }

    @Override
    public ProductFailureReport save(ProductFailureReport productFailureReport) {
        return jpaProductFailureReportRepository.save(productFailureReport);
    }

    @Override
    public boolean isAnyConfirmedComplaintInProgressForProductOrderById(Long productOrderId) {
        return jpaProductFailureReportRepository.isAnyConfirmedComplaintInProgressForProductOrderById(productOrderId,new Date(LocalDate.now().toEpochDay()));
    }

    @Override
    public List<ProductFailureReport> findAllByManagerUsername(String username) {
        return jpaProductFailureReportRepository.findAllByManagerUsername(username);
    }
}
