package com.app.infrastructure.controller;

import com.app.application.service.ShopService;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.UpdateShopDto;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.createShop.CreateStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops") /*ADMIN_SHOP*/
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<ResponseData<List<ShopDto>>> getAll() throws InterruptedException {

        var body = ResponseData.<List<ShopDto>>builder()
                .data(shopService.getAllShops())
                .build();

        return ResponseEntity.ok(body);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ShopDto>> getOne(@PathVariable Long id) {

        var body = ResponseData.<ShopDto>builder()
                .data(shopService.getShopById(id))
                .build();

        return ResponseEntity.ok(body);
    }

    @PostMapping(
            consumes = {"application/xml", "application/json"})
    public ResponseEntity<ResponseData<Long>> add(RequestEntity<CreateShopDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(shopService.addShop(requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        shopService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<Long>> update(@PathVariable Long id, RequestEntity<UpdateShopDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(shopService.updateShop(id, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @PostMapping("{shopId}/stocks")
    public ResponseEntity<ResponseData<Long>> addStockToExistingShop(
            @PathVariable Long shopId,
            RequestEntity<CreateStockDto> requestEntity
    ) {

        var body = ResponseData.<Long>builder()
                .data(shopService.createStockForShop(shopId, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PatchMapping("/{shopId}/stocks/{stockId}")
    public ResponseEntity<ResponseData<Long>> updateStock(
            @PathVariable Long shopId,
            @PathVariable Long stockId,
            RequestEntity<Map<String, String>> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(shopService.updateStock(shopId, stockId, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @DeleteMapping("/{shopId}/stocks/{stockId}")
    public ResponseEntity<Void> deleteStock(
            @PathVariable Long shopId,
            @PathVariable Long stockId
    ) {

        shopService.deleteStock(shopId, stockId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

