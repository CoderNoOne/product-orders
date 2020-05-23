package com.app.infrastructure.controller;

import com.app.application.service.StockService;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.TransferProductDto;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks") /*USER_MANAGER*/
public class StockController {

    private final StockService stockService;

    @PostMapping(value = "/products")
    public ResponseEntity<ResponseData<Long>> addProductToStock(RequestEntity<AddProductToStockDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(stockService.addProductToStock(requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }

    @GetMapping("/{id}/products")
    public ResponseEntity<ResponseData<List<ProductQuantityDto>>> getAllProductsForStock(@PathVariable Long id) {

        var body = ResponseData.<List<ProductQuantityDto>>builder()
                .data(stockService.getAllProductsForStock(id))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @PutMapping(value = "/products")
    public ResponseEntity<ResponseData<Long>> relocateProductsBetweenStores2(RequestEntity<TransferProductDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(stockService.relocateProducts(requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
