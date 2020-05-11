package com.app.infrastructure.dto;

import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

    private Long id;
    private String address;
    private List<ProductQuantityDto> productsQuantity;

}
