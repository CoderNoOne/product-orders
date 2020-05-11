package com.app.infrastructure.dto;

import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferProductDto {

    private ProductInfo productInfo;
    private Integer quantity;
    private Long stockFrom;
    private Long stockTo;
}
