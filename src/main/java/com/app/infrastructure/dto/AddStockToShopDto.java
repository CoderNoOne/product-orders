package com.app.infrastructure.dto;

import com.app.domain.entity.Address;
import com.app.domain.entity.Shop;
import com.app.domain.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddStockToShopDto {

    private Long shopId;
    private String address;

    public Stock toEntity() {
        return Stock.builder()
                .shop(Shop.builder()
                        .id(shopId)
                        .build())
                .address(Address.builder()
                        .address(address)
                        .build())
                .build();
    }
}
