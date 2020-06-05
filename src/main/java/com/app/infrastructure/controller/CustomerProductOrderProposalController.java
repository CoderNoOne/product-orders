package com.app.infrastructure.controller;

import com.app.application.service.CustomerProductOrderProposalService;
import com.app.infrastructure.dto.CreateProductOrderProposalByCustomerDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateProductOrderProposalByCustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/product-order-proposals")
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductOrderProposalDto>> getProposals(@RequestParam(name = "status", required = false) String status) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ProductOrderProposalDto>>builder()
                .data(Objects.nonNull(status) ?
                        customerProductOrderProposalService.getProposalsByStatus(username, status) :
                        customerProductOrderProposalService.getAllProposals(username))
                .build();

    }

    @PutMapping("/{id}/deny")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> denyProductOrderProposal(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(customerProductOrderProposalService.denyProductOrderProposal(id, username))
                .build();
    }

    @PutMapping("/{id}/reply")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> replyToProductOrderProposal(
            @PathVariable Long id,
            @RequestBody UpdateProductOrderProposalByCustomerDto updateProductOrderProposalByCustomerDto
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(customerProductOrderProposalService.replyToProductOrderProposal(id, username, updateProductOrderProposalByCustomerDto))
                .build();
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> acceptProductOrderProposal(
            @PathVariable Long id){

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(customerProductOrderProposalService.acceptProductOrderProposal(id, username))
                .build();

    }

    @GetMapping("/{id}/allRevisions")
    public ResponseData<List<ProductOrderProposalDto>> getById(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ProductOrderProposalDto>>builder()
                .data(customerProductOrderProposalService.getById(id, username))
                .build();

    }


}
