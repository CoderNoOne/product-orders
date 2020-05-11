package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDto {

    private Long id;
    private String name;
    private String address;
    private BigDecimal budget;
    private List<StockDto> stocks;
}
