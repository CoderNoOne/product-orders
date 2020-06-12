package com.app.infrastructure.controller;

import com.app.application.service.EmailService;
import com.app.application.service.MailTemplates;
import com.app.application.service.ProductFailureOnGuaranteeReportService;
import com.app.infrastructure.dto.CreateProductFailureOnGuaranteeReportDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-failures-on-guarantee")
public class ProductFailureOnGuaranteeReportController {

    private final ProductFailureOnGuaranteeReportService productFailureOnGuaranteeReportService;
    private final EmailService emailService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> save(@RequestBody CreateProductFailureOnGuaranteeReportDto requestBody) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var saved = productFailureOnGuaranteeReportService.save(requestBody, username);

        emailService.sendAsHtml(
                null,
                saved.getProductOrderDto().getCustomerDto().getEmail(),
                MailTemplates.generateHtmlInfoAboutCompletionDate(saved),
                "");

        return ResponseData.<Long>builder()
                .data(saved.getId())
                .build();


    }
}
