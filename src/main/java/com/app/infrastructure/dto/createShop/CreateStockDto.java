package com.app.infrastructure.dto.createShop;

import com.app.domain.entity.Address;
import com.app.domain.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStockDto {

    private String address;

    public Stock toEntity() {
        return Stock.builder()
                .address(Address.builder().address(address).build())
                .productsQuantity(new HashMap<>())
                .build();
    }
}
