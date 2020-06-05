package com.app.application.service;

import com.app.domain.entity.ProductFailureReport;
import com.app.domain.repository.ProductFailureReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductFailureReportService {

    private final ProductFailureReportRepository productFailureReportRepository;

    public List<ProductFailureReport> getAllForUsername(String username){

        return productFailureReportRepository.findAllByUsername(username);
    }
}
