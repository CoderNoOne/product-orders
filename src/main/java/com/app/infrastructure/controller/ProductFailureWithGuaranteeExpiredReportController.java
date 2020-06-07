package com.app.infrastructure.controller;

import com.app.application.service.ProductFailureWithGuaranteeExpiredReportService;
import com.app.infrastructure.dto.CreateProductFailureWithGuaranteeExpiredReportByManagerDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-failures-with-guarantee-expired")
public class ProductFailureWithGuaranteeExpiredReportController {

    private final ProductFailureWithGuaranteeExpiredReportService productFailureWithGuaranteeExpiredReportService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> save(@RequestBody CreateProductFailureWithGuaranteeExpiredReportByManagerDto createProductFailureWithGuaranteeExpiredReportByManagerDto){

        String managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(productFailureWithGuaranteeExpiredReportService.save(createProductFailureWithGuaranteeExpiredReportByManagerDto, managerUsername))
                .build();

    }
}
