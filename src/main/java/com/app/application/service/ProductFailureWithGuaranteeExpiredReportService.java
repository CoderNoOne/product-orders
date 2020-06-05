package com.app.application.service;

import com.app.domain.repository.ProductFailureWithGuaranteeExpiredReportRepository;
import com.app.infrastructure.dto.CreateProductFailureWithGuaranteeExpiredReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductFailureWithGuaranteeExpiredReportService {

    private final ProductFailureWithGuaranteeExpiredReportRepository productFailureWithGuaranteeExpiredReportRepository;

    public Long save(CreateProductFailureWithGuaranteeExpiredReportDto createProductFailureWithGuaranteeExpiredReportDto){

        //walidacja

    return null;
    }
}
