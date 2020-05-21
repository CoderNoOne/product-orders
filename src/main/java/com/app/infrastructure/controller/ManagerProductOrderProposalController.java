package com.app.infrastructure.controller;

import com.app.application.service.ManagerProductOrderProposalService;
import com.app.infrastructure.dto.CreateManagerProductOrderProposalDto;
import com.app.infrastructure.dto.ManagerUpdateProductOrderProposalDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/managerProductOrderProposals")
public class ManagerProductOrderProposalController {

    private final ManagerProductOrderProposalService managerProductOrderProposalService;

    @PostMapping
    public ResponseEntity<ResponseData<Long>> addProductOrderProposal(
            @RequestBody CreateManagerProductOrderProposalDto createManagerProductOrderProposalDto) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.addManagerProductOrderProposal(username, createManagerProductOrderProposalDto))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<ResponseData<List<ProductOrderProposalDto>>> getAllProductOrderProposal() {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<List<ProductOrderProposalDto>>builder()
                .data(managerProductOrderProposalService.getAllProposals(managerUsername))
                .build();

        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<Long>> updateProductOrderProposal(
            @PathVariable Long id,
            RequestEntity<ManagerUpdateProductOrderProposalDto> requestEntity
    ) {
        var body = ResponseData.<Long>builder()
                .data(managerProductOrderProposalService.updateProductProposal(id, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
