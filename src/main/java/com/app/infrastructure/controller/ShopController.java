package com.app.infrastructure.controller;

import com.app.application.service.ShopService;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.UpdateShopDto;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ShopDto>> getAll(@RequestParam(name = "productInStore", required = false) Long id) {

        return ResponseData.<List<ShopDto>>builder()
                .data(Objects.isNull(id) ? shopService.getAllShops() : shopService.getAllShopsWithProductInStore(id))
                .build();

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ShopDto> getOne(@PathVariable Long id) {

        return ResponseData.<ShopDto>builder()
                .data(shopService.getShopById(id))
                .build();
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> add(@RequestBody CreateShopDto createShopDto) {

        return ResponseData.<Long>builder()
                .data(shopService.addShop(createShopDto))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        shopService.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> update(@PathVariable Long id, @RequestBody UpdateShopDto updateShopDto) {

        return ResponseData.<Long>builder()
                .data(shopService.updateShop(id, updateShopDto))
                .build();

    }

    @PostMapping("{shopId}/stocks")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addStockToExistingShop(
            @PathVariable Long shopId,
            @RequestBody CreateStockDto createStockDto
    ) {

        return ResponseData.<Long>builder()
                .data(shopService.createStockForShop(shopId, createStockDto))
                .build();
    }

    @PatchMapping("/{shopId}/stocks/{stockId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> updateStock(
            @PathVariable Long shopId,
            @PathVariable Long stockId,
            @RequestBody Map<String, String> properties) {

        return ResponseData.<Long>builder()
                .data(shopService.updateStock(shopId, stockId, properties))
                .build();

    }

    @DeleteMapping("/{shopId}/stocks/{stockId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStock(
            @PathVariable Long shopId,
            @PathVariable Long stockId
    ) {
        shopService.deleteStock(shopId, stockId);
    }

}

