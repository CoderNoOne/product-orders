package com.app.infrastructure.dto.createShop;

import com.app.domain.entity.Address;
import com.app.domain.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShopDto {

    private String name;
    private BigDecimal budget;
    private String address;

    private Set<CreateStockDto> stocks;

    public Shop toEntity() {
        return Shop.builder()
                .name(name)
                .budget(budget)
                .stocks(stocks != null ?
                        stocks.stream()
                                .map(CreateStockDto::toEntity)
                                .collect(Collectors.toSet()) : new HashSet<>())
                .address(Address.builder().address(address).build())
                .build();
    }
}
