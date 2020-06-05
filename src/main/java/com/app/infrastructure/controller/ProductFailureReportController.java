package com.app.infrastructure.controller;

import com.app.application.service.ProductFailureReportService;
import com.app.domain.entity.ProductFailureReport;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-failure-reports")
public class ProductFailureReportController {

    private final ProductFailureReportService productFailureReportService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductFailureReport>> getAll() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ProductFailureReport>>builder()
                .data(productFailureReportService.getAllForUsername(username))
                .build();

    }
}
