package com.app.infrastructure.controller;

import com.app.application.service.ProductFailureWithGuaranteeExpiredReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-failures-with-guarantee-expired")
public class ProductFailureWithGuaranteeExpiredReportController {

    private final ProductFailureWithGuaranteeExpiredReportService productFailureWithGuaranteeExpiredReportService;

}
