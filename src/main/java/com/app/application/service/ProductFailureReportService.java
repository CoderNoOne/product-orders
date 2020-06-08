package com.app.application.service;

import com.app.domain.entity.ProductFailureReport;
import com.app.domain.repository.ProductFailureReportRepository;
import com.app.infrastructure.dto.ProductFailureReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductFailureReportService {

    private final ProductFailureReportRepository productFailureReportRepository;

    public List<ProductFailureReportDto> getAllForCustomerUsername(String username) {

        return productFailureReportRepository.findAllByUsername(username)
                .stream()
                .map(ProductFailureReport::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductFailureReportDto> getAllForManagerUsername(String username) {
        return productFailureReportRepository.findAllByManagerUsername(username)
                .stream()
                .map(ProductFailureReport::toDto)
                .collect(Collectors.toList());
    }
}
