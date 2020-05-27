package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductOrderProposalByManagerDto {

    private BigDecimal discount;
    private String shopName;
    private Integer daysFromOrderToPaymentDeadline;

}
