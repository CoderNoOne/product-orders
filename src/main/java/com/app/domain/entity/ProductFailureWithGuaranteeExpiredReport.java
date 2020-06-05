package com.app.domain.entity;

import com.app.domain.enums.ProductFailureReportStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
@Audited

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "true")
public class ProductFailureWithGuaranteeExpiredReport extends ProductFailureReport{

    @Enumerated(EnumType.STRING)
    private ProductFailureReportStatus status;

    private BigDecimal repairCosts;
}
