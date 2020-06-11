package com.app.domain.entity;

import com.app.domain.enums.ProductFailureReportStatus;
import com.app.domain.enums.ProposalSide;
import com.app.infrastructure.dto.ProductFailureWithGuaranteeExpiredReportDto;
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
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Audited

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "true")
public class ProductFailureWithGuaranteeExpiredReport extends ProductFailureReport {

    @Enumerated(EnumType.STRING)
    private ProductFailureReportStatus status;

    private BigDecimal repairCosts;

    @Enumerated(EnumType.STRING)
    private ProposalSide side;

    public ProductFailureReportStatus getStatus() {
        return status;
    }

    public ProductFailureWithGuaranteeExpiredReport side(ProposalSide side) {
        this.side = side;
        return this;
    }

    public ProductFailureWithGuaranteeExpiredReport costs(BigDecimal costs) {
        this.repairCosts = costs;
        return this;
    }

    public ProductFailureWithGuaranteeExpiredReport completionDate(LocalDate completionDate) {
        setCompletionDate(completionDate);
        return this;
    }

    public BigDecimal getCosts() {
        return repairCosts;
    }

    public ProposalSide getSide() {
        return side;
    }

    public ProductFailureWithGuaranteeExpiredReport status(ProductFailureReportStatus status) {
        this.status = status;
        return this;
    }


    public ProductFailureWithGuaranteeExpiredReportDto toProductFailureWithGuaranteeExpiredReportDto(){
        return ProductFailureWithGuaranteeExpiredReportDto.builder()
                .id(getId())
                .status(status)
                .side(side)
                .costs(repairCosts)
                .completionDate(getCompletionDate())
                .damageType(getDamageType())
                .productOrderDto(Objects.nonNull(getProductOrder()) ? getProductOrder().toDto() : null)
                .build();
    }
}
