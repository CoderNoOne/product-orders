package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepairOrderDto {

    private Long id;
    private LocalDate completionDate;
    private BigDecimal repairCosts;
    private ProductOrderDto productOrder;
}
