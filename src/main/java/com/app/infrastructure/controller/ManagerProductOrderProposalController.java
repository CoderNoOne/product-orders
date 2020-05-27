package com.app.infrastructure.controller;

import com.app.application.service.ManagerProductOrderProposalService;
import com.app.infrastructure.dto.CreateManagerProductOrderProposalDto;
import com.app.infrastructure.dto.ManagerUpdateProductOrderProposalDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.ResponseData;
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
}
