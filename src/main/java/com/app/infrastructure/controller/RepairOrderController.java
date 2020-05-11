package com.app.infrastructure.controller;

import com.app.application.service.RepairOrderService;
import com.app.infrastructure.dto.CreateRepairOrderDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repairOrders")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @PostMapping
    public ResponseEntity<ResponseData<Long>> add(
            @AuthenticationPrincipal String username,
            RequestEntity<CreateRepairOrderDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(repairOrderService.addRepairOrder(username, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

}
