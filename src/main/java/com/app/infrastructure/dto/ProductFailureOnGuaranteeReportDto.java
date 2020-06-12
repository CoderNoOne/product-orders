package com.app.infrastructure.dto;

import com.app.domain.enums.DamageType;
import com.app.domain.enums.GuaranteeComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFailureOnGuaranteeReportDto {

    private Long id;
    private LocalDate completionDate;
    private GuaranteeComponent selectedService;
    private DamageType damageType;
    private ProductOrderDto productOrderDto;
}
