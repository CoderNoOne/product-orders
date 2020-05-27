package com.app.infrastructure.controller;

import com.app.application.service.ManagerProductOrderProposalService;
import com.app.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/productOrderProposals")
public class ManagerProductOrderProposalController {

    private final ManagerProductOrderProposalService managerProductOrderProposalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addProductOrderProposal(
            @RequestBody CreateManagerProductOrderProposalDto createManagerProductOrderProposalDto) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.addManagerProductOrderProposal(username, createManagerProductOrderProposalDto))
                .build();

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductOrderProposalDto>> getAllProductOrderProposal() {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ProductOrderProposalDto>>builder()
                .data(managerProductOrderProposalService.getAllProposals(managerUsername))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> updateProductOrderProposal(
            @PathVariable Long id,
            RequestEntity<ManagerUpdateProductOrderProposalDto> requestEntity
    ) {
        return ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.updateProductProposal(id, requestEntity.getBody()))
                .build();
    }

    @GetMapping("/{id}/allRevisions")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductOrderProposalDto>> getById(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ProductOrderProposalDto>>builder()
                .data(managerProductOrderProposalService.getRevisionsById(id, username))
                .build();

    }

    @PutMapping("/{id}/deny")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> denyProductOrderProposal(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.denyProductOrderProposal(id, username))
                .build();
    }

    @PutMapping("/{id}/reply")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> replyToProductOrderProposal(
            @PathVariable Long id,
            @RequestBody UpdateProductOrderProposalByManagerDto updateProductOrderProposalByManagerDto
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.replyToProductOrderProposal(id, username, updateProductOrderProposalByManagerDto))
                .build();
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> acceptProductOrderProposal(@PathVariable Long id){

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.acceptProductOrderProposal(id, username))
                .build();

    }
}
