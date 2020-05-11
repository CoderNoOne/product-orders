package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateProductDto {

    private String name;
    private BigDecimal price;
    private GuaranteeDto guaranteeDetails;
}
