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
public class UpdateProductFailureWithGuaranteeExpiredReportDto {

    private Long productReportFailureWithGuaranteeExpiredReportId;
    private BigDecimal costs;
    private LocalDate completionDate;

    public UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto byCustomer(String customerUsername) {

        return UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto
                .builder()
                .costs(costs)
                .completionDate(completionDate)
                .productFailureWithGuaranteeExpiredReportId(productReportFailureWithGuaranteeExpiredReportId)
                .customerUsername(customerUsername)
                .build();
    }

    public UpdateProductFailureWithGuaranteeExpiredReportByManagerDto byManager(String managerUsername) {
        return UpdateProductFailureWithGuaranteeExpiredReportByManagerDto.builder()
                .productFailureWithGuaranteeExpiredReportId(productReportFailureWithGuaranteeExpiredReportId)
                .completionDate(completionDate)
                .managerUsername(managerUsername)
                .costs(costs)
                .build();
    }
}
