package com.app.infrastructure.controller;

import com.app.application.service.CustomerProductOrderProposalService;
import com.app.infrastructure.dto.CreateProductOrderProposalByCustomerDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/productOrderProposals")
public class CustomerProductOrderProposalController {

    private final CustomerProductOrderProposalService customerProductOrderProposalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addProductOrderProposal(
            RequestEntity<CreateProductOrderProposalByCustomerDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(customerProductOrderProposalService.addProductOrderProposal(username, requestEntity.getBody()))
                .build();
    }
}
