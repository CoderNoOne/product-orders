package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto {

    private Long productFailureWithGuaranteeExpiredReportId;
    private BigDecimal costs;
    private LocalDate completionDate;

    private String customerUsername;

}
