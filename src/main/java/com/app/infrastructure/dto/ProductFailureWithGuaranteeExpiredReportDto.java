package com.app.infrastructure.dto;

import com.app.domain.enums.DamageType;
import com.app.domain.enums.ProductFailureReportStatus;
import com.app.domain.enums.ProposalSide;
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
public class ProductFailureWithGuaranteeExpiredReportDto {

    private Long id;

    private LocalDate completionDate;
    private DamageType damageType;
    private ProductOrderDto productOrderDto;

    private ProductFailureReportStatus status;
    private BigDecimal costs;
    private ProposalSide side;
}
