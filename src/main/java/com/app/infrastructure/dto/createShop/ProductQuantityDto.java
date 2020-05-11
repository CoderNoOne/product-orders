package com.app.infrastructure.dto.createShop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductQuantityDto {

    private ProductInfo productInfo;
    private Integer quantity;
}
