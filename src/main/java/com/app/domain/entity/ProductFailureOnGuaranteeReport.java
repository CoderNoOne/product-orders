package com.app.domain.entity;

import com.app.domain.enums.GuaranteeComponent;
import com.app.infrastructure.dto.ProductFailureOnGuaranteeReportDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Objects;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = "false")
public class ProductFailureOnGuaranteeReport extends ProductFailureReport {

    @Enumerated(EnumType.STRING)
    private GuaranteeComponent selectedServiceType;


    public ProductFailureOnGuaranteeReportDto toProductFailureOnGuaranteeReportDto(){
        return ProductFailureOnGuaranteeReportDto.builder()
                .id(getId())
                .completionDate(getCompletionDate())
                .selectedService(selectedServiceType)
                .productOrderDto(Objects.nonNull(getProductOrder()) ? getProductOrder().toDto() : null)
                .build();
    }
}
