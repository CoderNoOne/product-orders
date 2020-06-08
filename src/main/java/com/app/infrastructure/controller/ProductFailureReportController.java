package com.app.infrastructure.controller;

import com.app.application.service.ProductFailureReportService;
import com.app.application.service.UserService;
import com.app.infrastructure.dto.ProductFailureReportDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-failure-reports")
public class ProductFailureReportController {

    private final ProductFailureReportService productFailureReportService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductFailureReportDto>> getAll() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ProductFailureReportDto>>builder()
                .data(userService.isManager(username) ?
                        productFailureReportService.getAllForManagerUsername(username)
                        :productFailureReportService.getAllForCustomerUsername(username))
                .build();

    }
}
