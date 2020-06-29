package com.app.infrastructure.controller;

import com.app.application.service.AdminShopPropertyService;
import com.app.infrastructure.dto.AdminShopPropertyDto;
import com.app.infrastructure.dto.CreateAdminShopPropertyDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateAdminShopPropertyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/adminShopProperties")
@RequiredArgsConstructor
public class AdminShopPropertyController {

    private final AdminShopPropertyService adminShopPropertyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addProperty(RequestEntity<CreateAdminShopPropertyDto> requestEntity) {

        return ResponseData.<Long>builder()
                .data(adminShopPropertyService.addProperty(requestEntity.getBody()))
                .build();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> updatePropertyValue(RequestEntity<UpdateAdminShopPropertyDto> requestEntity) {

        return ResponseData.<Long>builder()
                .data(adminShopPropertyService.updatePropertyValue(requestEntity.getBody()))
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<AdminShopPropertyDto>> getAllProperties() {

        return ResponseData.<List<AdminShopPropertyDto>>builder()
                .data(adminShopPropertyService.getAllProperties())
                .build();

    }

    @GetMapping("/{propertyName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<BigDecimal> getPropertyValueByName(@PathVariable String propertyName) {

        return ResponseData.<BigDecimal>builder()
                .data(adminShopPropertyService.getPropertyValueByName(propertyName))
                .build();
    }

    @DeleteMapping("/{propertyName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePropertyValue(@PathVariable String propertyName) {

        adminShopPropertyService.deletePropertyValue(propertyName);

    }
}
