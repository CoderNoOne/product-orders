package com.app.infrastructure.controller;

import com.app.application.service.ProductFailureWithGuaranteeExpiredReportService;
import com.app.application.service.UserService;
import com.app.infrastructure.dto.CreateProductFailureWithGuaranteeExpiredReportByManagerDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateProductFailureWithGuaranteeExpiredReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-failures-with-guarantee-expired")
public class ProductFailureWithGuaranteeExpiredReportController {

    private final ProductFailureWithGuaranteeExpiredReportService productFailureWithGuaranteeExpiredReportService;
    private final UserService userService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> save(@RequestBody CreateProductFailureWithGuaranteeExpiredReportByManagerDto createProductFailureWithGuaranteeExpiredReportByManagerDto) {

        String managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(productFailureWithGuaranteeExpiredReportService.save(createProductFailureWithGuaranteeExpiredReportByManagerDto, managerUsername))
                .build();

    }

    @PutMapping("/reply")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> reply( @RequestBody UpdateProductFailureWithGuaranteeExpiredReportDto updateProductFailureWithGuaranteeExpiredReportDto) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(userService.isManager(username) ? productFailureWithGuaranteeExpiredReportService.replyToByManager(updateProductFailureWithGuaranteeExpiredReportDto.byManager(username)) :
                        productFailureWithGuaranteeExpiredReportService.replyToByCustomer(updateProductFailureWithGuaranteeExpiredReportDto.byCustomer(username)))
                .build();
    }

    @PutMapping("{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> accept(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(userService.isManager(username) ? productFailureWithGuaranteeExpiredReportService.acceptByManager(id, username) :
                        productFailureWithGuaranteeExpiredReportService.acceptByCustomer(id, username))
                .build();

    }


}
