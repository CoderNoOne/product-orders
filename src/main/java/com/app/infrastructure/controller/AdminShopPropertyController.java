package com.app.infrastructure.controller;

import com.app.application.service.AdminShopPropertyService;
import com.app.infrastructure.dto.AdminShopPropertyDto;
import com.app.infrastructure.dto.CreateAdminShopPropertyDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateAdminShopPropertyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/adminShopProperties")
@RequiredArgsConstructor
public class AdminShopPropertyController {

    private final AdminShopPropertyService adminShopPropertyService;

    @PostMapping
    public ResponseEntity<ResponseData<Long>> addProperty(RequestEntity<CreateAdminShopPropertyDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(adminShopPropertyService.addProperty(requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }

    @PatchMapping
    public ResponseEntity<ResponseData<Long>> updatePropertyValue(RequestEntity<UpdateAdminShopPropertyDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(adminShopPropertyService.updatePropertyValue(requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<ResponseData<List<AdminShopPropertyDto>>> getAllProperties() {

        var body = ResponseData.<List<AdminShopPropertyDto>>builder()
                .data(adminShopPropertyService.getAllProperties())
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{propertyName}")
    public ResponseEntity<ResponseData<BigDecimal>> getPropertyValueByName(@PathVariable String propertyName) {

        var body = ResponseData.<BigDecimal>builder()
                .data(adminShopPropertyService.getPropertyValueByName(propertyName))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @DeleteMapping("/{propertyName}")
    public ResponseEntity<Void> deletePropertyValue(@PathVariable String propertyName){

        adminShopPropertyService.deletePropertyValue(propertyName);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
