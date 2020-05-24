package com.app.infrastructure.controller;

import com.app.application.service.ProductService;
import com.app.application.service.ShopService;
import com.app.infrastructure.dto.ProductDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.UpdateProductDto;
import com.app.infrastructure.dto.createProduct.CreateProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RequestMapping("/products")
@RequiredArgsConstructor
@RestController
public class ProductController { /*ADMIN_PRODUCT*/

    private final ProductService productService;
    private final ShopService shopService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductDto>> getProducts(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "producer", required = false) String producer,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice

    ) {

        return ResponseData.<List<ProductDto>>builder()
                .data(productService.getFilteredProducts(category, producer, minPrice, maxPrice))
                .build();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ProductDto> getOne(
            @PathVariable Long id) {

        return ResponseData.<ProductDto>builder()
                .data(productService.getById(id))
                .build();
    }


    @PostMapping(consumes = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> add(
            RequestEntity<CreateProductDto> requestEntity) {

        return ResponseData.<Long>builder()
                .data(productService.addProduct(requestEntity.getBody()))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @PutMapping("{/id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> update(
            @PathVariable Long id,
            RequestEntity<UpdateProductDto> requestEntity
    ) {

        return ResponseData.<Long>builder()
                .data(productService.updateProduct(id, requestEntity.getBody()))
                .build();
    }

    @GetMapping("/{id}/shops")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ShopDto>> getAllShopsWithProductInStore(
            @PathVariable Long id) {

        return ResponseData.<List<ShopDto>>builder()
                .data(shopService.getAllShopsWithProductInStore(id))
                .build();
    }


    @GetMapping("/{id}/productQuantityGroupByShop")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Map<String, Integer>> getProductQuantityGroupByShop(@PathVariable Long id) {

        return ResponseData.<Map<String, Integer>>builder()
                .data(shopService.findProductQuantityGroupByShop(id))
                .build();

    }

}
