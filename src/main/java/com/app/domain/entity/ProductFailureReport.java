package com.app.domain.entity;

import com.app.domain.enums.DamageType;
import com.app.domain.enums.ProductFailureReportStatus;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ProductFailureReportDto;
import com.app.infrastructure.dto.RepairOrderDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited(targetAuditMode = NOT_AUDITED)
@AuditOverride(forClass = ProductFailureReport.class, isAudited = true)


@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "product_failure_reports")
@DiscriminatorColumn(name = "guarantee_expired", discriminatorType = DiscriminatorType.STRING)
public abstract class ProductFailureReport extends BaseEntity {

    private LocalDate completionDate;

    @Enumerated(EnumType.STRING)
    private DamageType damageType;

    @OneToOne
    @JoinColumn(name = "product_order_id", referencedColumnName = "id")
    private ProductOrder productOrder;

    public LocalDate getCompletionDate() {
        return completionDate;
    }


    public ProductFailureReportDto toDto() {
        return ProductFailureReportDto.builder()
                .id(getId())
                .completionDate(completionDate)
                .damageType(Objects.nonNull(damageType) ? damageType.name() : null)
                .productOrderDto(Objects.nonNull(productOrder) ? productOrder.toDto() : null)
                .build();
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public ProductOrder getProductOrder() {
        return productOrder;
    }
}
