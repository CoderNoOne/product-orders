package com.app.infrastructure.controller;

import com.app.application.service.StockService;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.TransferProductDto;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks") /*USER_MANAGER*/
public class StockController {

    private final StockService stockService;

    @PostMapping(value = "/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addProductToStock(RequestEntity<AddProductToStockDto> requestEntity) {

        return ResponseData.<Long>builder()
                .data(stockService.addProductToStock(requestEntity.getBody()))
                .build();

    }

    @GetMapping("/{id}/products")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductQuantityDto>> getAllProductsForStock(@PathVariable Long id) {

        return ResponseData.<List<ProductQuantityDto>>builder()
                .data(stockService.getAllProductsForStock(id))
                .build();
    }

    @PutMapping(value = "/products")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> relocateProductsBetweenStores2(RequestEntity<TransferProductDto> requestEntity) {

        return ResponseData.<Long>builder()
                .data(stockService.relocateProducts(requestEntity.getBody()))
                .build();
    }
}
