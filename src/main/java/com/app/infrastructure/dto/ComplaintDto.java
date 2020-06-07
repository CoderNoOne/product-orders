package com.app.infrastructure.dto;

import com.app.domain.enums.DamageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintDto {

    private Long id;
    private ProductOrderDto productOrderDto;
    private LocalDate issueDate;
    private DamageType damageType;
    private String complaintStatus;
}

