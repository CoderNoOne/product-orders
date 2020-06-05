//package com.app.infrastructure.controller;
//
//import com.app.application.service.RepairOrderService;
//import com.app.infrastructure.dto.CreateRepairOrderDto;
//import com.app.infrastructure.dto.RepairOrderDto;
//import com.app.infrastructure.dto.ResponseData;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/repairOrders")
//public class RepairOrderController {
//
//    private final RepairOrderService repairOrderService;
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseData<Long> add(
//            RequestEntity<CreateRepairOrderDto> requestEntity) {
//
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return ResponseData.<Long>builder()
//                .data(repairOrderService.addRepairOrder(username, requestEntity.getBody()))
//                .build();
//    }
//
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseData<List<RepairOrderDto>> getAllRepairOrders() {
//
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return ResponseData.<List<RepairOrderDto>>builder()
//                .data(repairOrderService.getAll(username))
//                .build();
//
//    }
//
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseData<RepairOrderDto> getById(@PathVariable Long id) {
//
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return ResponseData.<RepairOrderDto>builder()
//                .data(repairOrderService.getOne(id, username))
//                .build();
//    }
//}
