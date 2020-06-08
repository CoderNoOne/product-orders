package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFailureReportDto {

    private Long id;
    private LocalDate completionDate;
    private String damageType;
    private ProductOrderDto productOrderDto;

}
