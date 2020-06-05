package com.app.infrastructure.dto;

import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import com.app.domain.enums.ProductFailureReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductFailureWithGuaranteeExpiredReportDto {

    private String status;
    private Long complaintId;
    private LocalDate completionDate;
    private BigDecimal costs;
}
   /* public ProductFailureWithGuaranteeExpiredReport toEntity() {

        return ProductFailureWithGuaranteeExpiredReport.builder()
                .repairCosts(costs)
                .completionDate(completionDate)
                .status(Objects.nonNull(status) ? Arrays.stream(ProductFailureReportStatus.values()) :)
                .build();
    }
}*/
