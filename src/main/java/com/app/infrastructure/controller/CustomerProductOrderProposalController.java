package com.app.infrastructure.controller;

import com.app.application.service.ProductOrderProposalService;
import com.app.infrastructure.dto.CreateProductOrderProposalByCustomerDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/productOrderProposals")
public class CustomerProductOrderProposalController {

    private final ProductOrderProposalService productOrderProposalService;

    @PostMapping
    public ResponseEntity<ResponseData<Long>> addProductOrderProposal(
            /*@AuthenticationPrincipal String username,*/
            RequestEntity<CreateProductOrderProposalByCustomerDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<Long>builder()
                .data(productOrderProposalService.addProductOrderProposal(username, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }
}
