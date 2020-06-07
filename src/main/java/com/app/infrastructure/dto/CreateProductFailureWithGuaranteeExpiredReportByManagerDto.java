package com.app.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CreateProductFailureWithGuaranteeExpiredReportByManagerDto {

    private Long complaintId;
    private LocalDate completionDate;
    private BigDecimal costs;

    @JsonIgnore
    private String managerUsername;

}

