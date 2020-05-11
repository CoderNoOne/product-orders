package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderFilteringCriteriaDto {

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private LocalDate fromDate;
    private LocalDate toDate;

    private Integer minQuantity;
    private Integer maxQuantity;

    private String category;
    private String productName;
    private String producerName;

}
