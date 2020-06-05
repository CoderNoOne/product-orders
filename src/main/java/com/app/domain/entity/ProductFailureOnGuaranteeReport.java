package com.app.domain.entity;

import com.app.domain.enums.GuaranteeComponent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = "false")
public class ProductFailureOnGuaranteeReport extends ProductFailureReport {

    @Enumerated(EnumType.STRING)
    private GuaranteeComponent selectedServiceType;
}
