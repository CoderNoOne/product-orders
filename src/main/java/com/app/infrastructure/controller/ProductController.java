package com.app.infrastructure.controller;

import com.app.application.service.ProductService;
import com.app.application.service.ShopService;
import com.app.application.validators.impl.CreateProductDtoValidator;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.createProduct.CreateProductDto;
import com.app.infrastructure.dto.ProductDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


@RequestMapping("/products")
@RequiredArgsConstructor
@RestController
public class ProductController { /*ADMIN_PRODUCT*/

    private final ProductService productService;
    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<ResponseData<List<ProductDto>>> getProducts(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "producer", required = false) String producer,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice

    ) {

        var body = ResponseData.<List<ProductDto>>builder()
                .data(productService.getFilteredProducts(category, producer, minPrice, maxPrice))
                .build();

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ProductDto>> getOne(
            @PathVariable Long id) {

        var body = ResponseData.<ProductDto>builder()
                .data(productService.getById(id))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping(
            consumes = {"application/json", "application/xml"})
    public ResponseEntity<ResponseData<Long>> add(
            RequestEntity<CreateProductDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(productService.addProduct(requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        productService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{/id}")
    public ResponseEntity<ResponseData<Long>> update(
            @PathVariable Long id,
            RequestEntity<UpdateProductDto> requestEntity
    ) {

        var body = ResponseData.<Long>builder()
                .data(productService.updateProduct(id, requestEntity.getBody()))
                .build();


        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{id}/shops")
    public ResponseEntity<ResponseData<List<ShopDto>>> getAllShopsWithProductInStore(
            @PathVariable Long id) {

        var body = ResponseData.<List<ShopDto>>builder()
                .data(shopService.getAllShopsWithProductInStore(id))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
